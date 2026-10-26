package com.rentaherramientas.modules.reservas.application.service;

import com.rentaherramientas.common.exception.BusinessRuleException;
import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.reservas.application.dto.CrearReservaCommand;
import com.rentaherramientas.modules.reservas.application.dto.DetalleReservaItemCommand;
import com.rentaherramientas.modules.reservas.domain.model.DetalleReserva;
import com.rentaherramientas.modules.reservas.domain.model.EstadoReserva;
import com.rentaherramientas.modules.reservas.domain.model.RangoFechas;
import com.rentaherramientas.modules.reservas.domain.model.Reserva;
import com.rentaherramientas.modules.reservas.domain.ports.in.CambiarEstadoReservaUseCase;
import com.rentaherramientas.modules.reservas.domain.ports.in.ConsultarReservaUseCase;
import com.rentaherramientas.modules.reservas.domain.ports.in.CrearReservaUseCase;
import com.rentaherramientas.modules.reservas.domain.ports.out.AuditoriaReservaPort;
import com.rentaherramientas.modules.reservas.domain.ports.out.ReservaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaApplicationService implements CrearReservaUseCase, ConsultarReservaUseCase, CambiarEstadoReservaUseCase {

    private final ReservaRepositoryPort reservaRepositoryPort;
    private final AuditoriaReservaPort auditoriaReservaPort;

    @Override
    @Transactional
    public Reserva crearReserva(CrearReservaCommand command) {
        RangoFechas rango = new RangoFechas(command.getFechaInicio(), command.getFechaFin());

        // Validar conflicto de disponibilidad para cada herramienta en el rango
        for (DetalleReservaItemCommand item : command.getItems()) {
            boolean conflicto = reservaRepositoryPort.existeConflictoFechas(
                    item.getIdHerramienta(),
                    rango.getFechaInicio(),
                    rango.getFechaFin()
            );
            if (conflicto) {
                throw new BusinessRuleException("La herramienta con ID " + item.getIdHerramienta() +
                        " ya tiene una reserva activa en las fechas seleccionadas (" +
                        rango.getFechaInicio() + " al " + rango.getFechaFin() + ")");
            }
        }

        int dias = (int) rango.getDiasAlquiler();
        List<DetalleReserva> detalles = new ArrayList<>();
        for (DetalleReservaItemCommand item : command.getItems()) {
            DetalleReserva detalle = DetalleReserva.builder()
                    .idHerramienta(item.getIdHerramienta())
                    .nombreHerramienta(item.getNombreHerramienta())
                    .cantidad(item.getCantidad())
                    .precioUnitarioDia(item.getPrecioUnitarioDia())
                    .diasAlquiler(dias)
                    .build();
            detalle.calcularSubtotal();
            detalles.add(detalle);
        }

        String codigoGenerado = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Reserva nuevaReserva = Reserva.builder()
                .codigoReserva(codigoGenerado)
                .idCliente(command.getIdCliente())
                .fechaSolicitud(LocalDateTime.now())
                .rangoFechas(rango)
                .estado(EstadoReserva.SOLICITADA)
                .detalles(detalles)
                .notasCliente(command.getNotasCliente())
                .build();

        nuevaReserva.calcularTotales();

        Reserva guardada = reservaRepositoryPort.guardar(nuevaReserva);

        // Auditoría inicial
        auditoriaReservaPort.registrarCambioEstado(
                guardada.getIdReserva(),
                null,
                EstadoReserva.SOLICITADA.name(),
                command.getIdCliente(),
                "Solicitud de reserva creada por el cliente"
        );

        return guardada;
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva obtenerPorId(Long id) {
        return reservaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la reserva con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Reserva obtenerPorCodigo(String codigo) {
        return reservaRepositoryPort.buscarPorCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la reserva con código: " + codigo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarPorCliente(Long idCliente) {
        return reservaRepositoryPort.buscarPorCliente(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> listarTodas() {
        return reservaRepositoryPort.buscarTodas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reserva> filtrarPorRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde == null && hasta == null) {
            return reservaRepositoryPort.buscarTodas();
        }
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        LocalDate limiteInferior = desde != null ? desde : LocalDate.of(1900, 1, 1);
        LocalDate limiteSuperior = hasta != null ? hasta : LocalDate.of(9999, 12, 31);
        return reservaRepositoryPort.buscarPorRangoFechaInicio(limiteInferior, limiteSuperior);
    }

    @Override
    @Transactional
    public Reserva aprobarReserva(Long id, Long usuarioId) {
        Reserva reserva = obtenerPorId(id);
        String estadoAnterior = reserva.getEstado().name();
        reserva.aprobar();
        Reserva actualizada = reservaRepositoryPort.guardar(reserva);

        auditoriaReservaPort.registrarCambioEstado(
                id,
                estadoAnterior,
                EstadoReserva.APROBADA.name(),
                usuarioId != null ? usuarioId : 1L,
                "Reserva aprobada por el proveedor/administrador"
        );
        return actualizada;
    }

    @Override
    @Transactional
    public Reserva rechazarReserva(Long id, String motivo, Long usuarioId) {
        Reserva reserva = obtenerPorId(id);
        String estadoAnterior = reserva.getEstado().name();
        reserva.rechazar(motivo);
        Reserva actualizada = reservaRepositoryPort.guardar(reserva);

        auditoriaReservaPort.registrarCambioEstado(
                id,
                estadoAnterior,
                EstadoReserva.RECHAZADA.name(),
                usuarioId != null ? usuarioId : 1L,
                "Reserva rechazada. Motivo: " + motivo
        );
        return actualizada;
    }

    @Override
    @Transactional
    public Reserva iniciarAlquiler(Long id, Long usuarioId) {
        Reserva reserva = obtenerPorId(id);
        String estadoAnterior = reserva.getEstado().name();
        reserva.iniciarAlquiler();
        Reserva actualizada = reservaRepositoryPort.guardar(reserva);

        auditoriaReservaPort.registrarCambioEstado(
                id,
                estadoAnterior,
                EstadoReserva.EN_CURSO.name(),
                usuarioId != null ? usuarioId : 1L,
                "Entrega de maquinaria confirmada. Alquiler en curso"
        );
        return actualizada;
    }

    @Override
    @Transactional
    public Reserva finalizarAlquiler(Long id, Long usuarioId) {
        Reserva reserva = obtenerPorId(id);
        String estadoAnterior = reserva.getEstado().name();
        reserva.finalizarAlquiler();
        Reserva actualizada = reservaRepositoryPort.guardar(reserva);

        auditoriaReservaPort.registrarCambioEstado(
                id,
                estadoAnterior,
                EstadoReserva.FINALIZADA.name(),
                usuarioId != null ? usuarioId : 1L,
                "Devolución recibida y alquiler finalizado"
        );
        return actualizada;
    }

    @Override
    @Transactional
    public Reserva cancelarReserva(Long id, Long usuarioId) {
        Reserva reserva = obtenerPorId(id);
        String estadoAnterior = reserva.getEstado().name();
        reserva.cancelar();
        Reserva actualizada = reservaRepositoryPort.guardar(reserva);

        auditoriaReservaPort.registrarCambioEstado(
                id,
                estadoAnterior,
                EstadoReserva.CANCELADA.name(),
                usuarioId != null ? usuarioId : 1L,
                "Reserva cancelada"
        );
        return actualizada;
    }
}
