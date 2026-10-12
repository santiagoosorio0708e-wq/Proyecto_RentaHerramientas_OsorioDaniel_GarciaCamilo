package com.rentaherramientas.modules.pagos.application.service;

import com.rentaherramientas.common.exception.BusinessRuleException;
import com.rentaherramientas.modules.pagos.application.dto.ProcesarPagoCommand;
import com.rentaherramientas.modules.pagos.domain.model.*;
import com.rentaherramientas.modules.pagos.domain.ports.out.FacturaRepositoryPort;
import com.rentaherramientas.modules.pagos.domain.ports.out.MetodoPagoRepositoryPort;
import com.rentaherramientas.modules.pagos.domain.ports.out.PagoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoApplicationServiceTest {

    @Mock
    private PagoRepositoryPort pagoRepositoryPort;

    @Mock
    private FacturaRepositoryPort facturaRepositoryPort;

    @Mock
    private MetodoPagoRepositoryPort metodoPagoRepositoryPort;

    @InjectMocks
    private PagoApplicationService pagoService;

    private ProcesarPagoCommand command;
    private MetodoPago metodoActivo;

    @BeforeEach
    void setUp() {
        metodoActivo = MetodoPago.builder()
                .idMetodoPago(1L)
                .nombre("PSE_BANCOLOMBIA")
                .descripcion("Transferencia PSE")
                .activo(true)
                .build();

        command = ProcesarPagoCommand.builder()
                .idReserva(10L)
                .idCliente(8L)
                .idMetodoPago(1L)
                .monto(new BigDecimal("119000.00")) // Total con IVA fácil de comprobar
                .tipoPago(TipoPago.TOTALIDAD)
                .descripcionServicio("Alquiler Mezcladora de concreto")
                .build();
    }

    @Test
    @DisplayName("Debe procesar el pago exitosamente y generar automáticamente la factura con IVA 19%")
    void procesarPago_ExitoConFacturaEmitida() {
        when(metodoPagoRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(metodoActivo));
        when(pagoRepositoryPort.guardar(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            return Pago.builder()
                    .idPago(50L)
                    .numeroTransaccion(p.getNumeroTransaccion())
                    .idReserva(p.getIdReserva())
                    .idMetodoPago(p.getIdMetodoPago())
                    .monto(p.getMonto())
                    .tipoPago(p.getTipoPago())
                    .estadoPago(p.getEstadoPago())
                    .fechaPago(p.getFechaPago())
                    .referenciaPasarela(p.getReferenciaPasarela())
                    .build();
        });
        when(facturaRepositoryPort.guardar(any(Factura.class))).thenAnswer(inv -> {
            Factura f = inv.getArgument(0);
            return Factura.builder()
                    .idFactura(20L)
                    .numeroFactura(f.getNumeroFactura())
                    .idPago(f.getIdPago())
                    .idCliente(f.getIdCliente())
                    .fechaEmision(f.getFechaEmision())
                    .subtotal(f.getSubtotal())
                    .iva(f.getIva())
                    .total(f.getTotal())
                    .estado(f.getEstado())
                    .urlComprobantePdf(f.getUrlComprobantePdf())
                    .detalles(f.getDetalles())
                    .build();
        });

        Pago resultado = pagoService.procesarPago(command);

        assertNotNull(resultado);
        assertEquals(50L, resultado.getIdPago());
        assertEquals(EstadoPago.APROBADO, resultado.getEstadoPago());
        assertTrue(resultado.getNumeroTransaccion().startsWith("TRX-"));
        assertTrue(resultado.getReferenciaPasarela().contains("PSE_BANCOLOMBIA"));

        // Verificación de factura adjunta
        assertNotNull(resultado.getFactura());
        assertEquals(20L, resultado.getFactura().getIdFactura());
        assertEquals(EstadoFactura.PAGADA, resultado.getFactura().getEstado());
        assertEquals(new BigDecimal("119000.00"), resultado.getFactura().getTotal());
        assertEquals(new BigDecimal("100000.00"), resultado.getFactura().getSubtotal()); // 119000 / 1.19
        assertEquals(new BigDecimal("19000.00"), resultado.getFactura().getIva()); // 119000 - 100000

        verify(pagoRepositoryPort).guardar(any(Pago.class));
        verify(facturaRepositoryPort).guardar(any(Factura.class));
    }

    @Test
    @DisplayName("Debe rechazar el procesamiento si el método de pago está inactivo")
    void procesarPago_MetodoInactivo_LanzaExcepcion() {
        MetodoPago inactivo = MetodoPago.builder()
                .idMetodoPago(2L)
                .nombre("CHEQUE")
                .activo(false)
                .build();

        command.setIdMetodoPago(2L);
        when(metodoPagoRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(inactivo));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                pagoService.procesarPago(command)
        );

        assertTrue(ex.getMessage().contains("no se encuentra habilitado"));
        verify(pagoRepositoryPort, never()).guardar(any());
        verify(facturaRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe consultar una factura existente por número de radicado")
    void obtenerFacturaPorNumero_Exito() {
        Factura factura = Factura.builder()
                .idFactura(1L)
                .numeroFactura("FAC-2026-0001")
                .total(new BigDecimal("500000.00"))
                .build();

        when(facturaRepositoryPort.buscarPorNumero("FAC-2026-0001")).thenReturn(Optional.of(factura));

        Factura resultado = pagoService.obtenerFacturaPorNumero("FAC-2026-0001");
        assertNotNull(resultado);
        assertEquals("FAC-2026-0001", resultado.getNumeroFactura());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el metodo de pago solicitado no existe")
    void procesarPago_MetodoNoExistente_LanzaExcepcion() {
        when(metodoPagoRepositoryPort.buscarPorId(999L)).thenReturn(Optional.empty());
        command.setIdMetodoPago(999L);

        assertThrows(RuntimeException.class, () ->
                pagoService.procesarPago(command)
        );
        verify(pagoRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si se consulta una factura inexistente")
    void obtenerFacturaPorNumero_NoExiste_LanzaExcepcion() {
        when(facturaRepositoryPort.buscarPorNumero("FAC-INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                pagoService.obtenerFacturaPorNumero("FAC-INEXISTENTE")
        );
    }
}

