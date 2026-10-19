package com.rentaherramientas.modules.pagos.application.service;

import com.rentaherramientas.common.exception.BusinessRuleException;
import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.pagos.application.dto.ProcesarPagoCommand;
import com.rentaherramientas.modules.pagos.domain.model.EstadoPago;
import com.rentaherramientas.modules.pagos.domain.model.Factura;
import com.rentaherramientas.modules.pagos.domain.model.MetodoPago;
import com.rentaherramientas.modules.pagos.domain.model.Pago;
import com.rentaherramientas.modules.pagos.domain.ports.in.ConsultarFacturaUseCase;
import com.rentaherramientas.modules.pagos.domain.ports.in.ConsultarPagoUseCase;
import com.rentaherramientas.modules.pagos.domain.ports.in.ProcesarPagoUseCase;
import com.rentaherramientas.modules.pagos.domain.ports.out.FacturaRepositoryPort;
import com.rentaherramientas.modules.pagos.domain.ports.out.MetodoPagoRepositoryPort;
import com.rentaherramientas.modules.pagos.domain.ports.out.PagoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Servicio de Aplicación: Orquestación de Pagos y Facturación Electrónica.
 * Implementa los casos de uso para procesar transacciones transaccionales,
 * validar el estado activo del método de pago, generar referencias únicas de pasarela
 * y emitir la factura electrónica correspondiente con desglose del 19% de IVA.
 *
 * @author Daniel Osorio (DEV 2)
 */
@Service
@RequiredArgsConstructor
public class PagoApplicationService implements ProcesarPagoUseCase, ConsultarPagoUseCase, ConsultarFacturaUseCase {

    private final PagoRepositoryPort pagoRepositoryPort;
    private final FacturaRepositoryPort facturaRepositoryPort;
    private final MetodoPagoRepositoryPort metodoPagoRepositoryPort;

    @Override
    @Transactional
    public Pago procesarPago(ProcesarPagoCommand command) {
        MetodoPago metodo = metodoPagoRepositoryPort.buscarPorId(command.getIdMetodoPago())
                .orElseThrow(() -> new ResourceNotFoundException("El método de pago con ID " + command.getIdMetodoPago() + " no existe"));

        if (!metodo.isActivo()) {
            throw new BusinessRuleException("El método de pago '" + metodo.getNombre() + "' no se encuentra habilitado");
        }

        String numeroTransaccion = "TRX-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        Pago nuevoPago = Pago.builder()
                .numeroTransaccion(numeroTransaccion)
                .idReserva(command.getIdReserva())
                .idMetodoPago(command.getIdMetodoPago())
                .monto(command.getMonto())
                .tipoPago(command.getTipoPago())
                .estadoPago(EstadoPago.PENDIENTE)
                .fechaPago(LocalDateTime.now())
                .build();

        // Simulación de pasarela de pago (Aprobación en línea)
        String refPasarela = "AUTH-" + metodo.getNombre() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        nuevoPago.aprobar(refPasarela);

        // Guardar pago
        Pago pagoGuardado = pagoRepositoryPort.guardar(nuevoPago);

        // Generar y asociar factura automática con IVA
        Factura facturaGenerada = pagoGuardado.generarFacturaAutomatica(command.getIdCliente(), command.getDescripcionServicio());
        Factura facturaGuardada = facturaRepositoryPort.guardar(facturaGenerada);

        // Reasociar factura para devolver objeto completo
        return Pago.builder()
                .idPago(pagoGuardado.getIdPago())
                .numeroTransaccion(pagoGuardado.getNumeroTransaccion())
                .idReserva(pagoGuardado.getIdReserva())
                .idMetodoPago(pagoGuardado.getIdMetodoPago())
                .monto(pagoGuardado.getMonto())
                .tipoPago(pagoGuardado.getTipoPago())
                .estadoPago(pagoGuardado.getEstadoPago())
                .fechaPago(pagoGuardado.getFechaPago())
                .referenciaPasarela(pagoGuardado.getReferenciaPasarela())
                .factura(facturaGuardada)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Pago obtenerPagoPorId(Long id) {
        Pago pago = pagoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pago con ID: " + id));

        facturaRepositoryPort.buscarPorPago(id).ifPresent(factura -> {
            pago.generarFacturaAutomatica(factura.getIdCliente(), "Recuperación de factura");
        });
        return pago;
    }

    @Override
    @Transactional(readOnly = true)
    public Pago obtenerPagoPorTransaccion(String transaccion) {
        return pagoRepositoryPort.buscarPorTransaccion(transaccion)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el pago con transacción: " + transaccion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> listarPagosPorReserva(Long idReserva) {
        return pagoRepositoryPort.buscarPorReserva(idReserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pago> listarTodosLosPagos() {
        return pagoRepositoryPort.buscarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public Factura obtenerFacturaPorId(Long id) {
        return facturaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la factura con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Factura obtenerFacturaPorNumero(String numeroFactura) {
        return facturaRepositoryPort.buscarPorNumero(numeroFactura)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la factura con número: " + numeroFactura));
    }

    @Override
    @Transactional(readOnly = true)
    public Factura obtenerFacturaPorPago(Long idPago) {
        return facturaRepositoryPort.buscarPorPago(idPago)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró factura vinculada al pago ID: " + idPago));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> listarFacturasPorCliente(Long idCliente) {
        return facturaRepositoryPort.buscarPorCliente(idCliente);
    }

    @Transactional(readOnly = true)
    public List<MetodoPago> listarMetodosPago() {
        return metodoPagoRepositoryPort.listarActivos();
    }
}
