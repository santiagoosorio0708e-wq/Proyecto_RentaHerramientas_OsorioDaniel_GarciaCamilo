package com.rentaherramientas.modules.reservas.application.service;

import com.rentaherramientas.common.exception.BusinessRuleException;
import com.rentaherramientas.modules.reservas.application.dto.CrearReservaCommand;
import com.rentaherramientas.modules.reservas.application.dto.DetalleReservaItemCommand;
import com.rentaherramientas.modules.reservas.domain.model.EstadoReserva;
import com.rentaherramientas.modules.reservas.domain.model.RangoFechas;
import com.rentaherramientas.modules.reservas.domain.model.Reserva;
import com.rentaherramientas.modules.reservas.domain.ports.out.AuditoriaReservaPort;
import com.rentaherramientas.modules.reservas.domain.ports.out.ReservaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaApplicationServiceTest {

    @Mock
    private ReservaRepositoryPort reservaRepositoryPort;

    @Mock
    private AuditoriaReservaPort auditoriaReservaPort;

    @InjectMocks
    private ReservaApplicationService reservaService;

    private CrearReservaCommand commandValido;

    @BeforeEach
    void setUp() {
        DetalleReservaItemCommand item = DetalleReservaItemCommand.builder()
                .idHerramienta(1L)
                .nombreHerramienta("Vibrocompactador Tipo Rana")
                .cantidad(1)
                .precioUnitarioDia(new BigDecimal("75000.00"))
                .build();

        commandValido = CrearReservaCommand.builder()
                .idCliente(8L)
                .fechaInicio(LocalDate.now().plusDays(1))
                .fechaFin(LocalDate.now().plusDays(4))
                .items(List.of(item))
                .notasCliente("Entrega en obra residencial")
                .build();
    }

    @Test
    @DisplayName("Debe crear una reserva exitosamente calculando total y depósito de garantía")
    void crearReserva_Exito() {
        when(reservaRepositoryPort.existeConflictoFechas(any(), any(), any())).thenReturn(false);
        when(reservaRepositoryPort.guardar(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            return Reserva.builder()
                    .idReserva(100L)
                    .codigoReserva(r.getCodigoReserva())
                    .idCliente(r.getIdCliente())
                    .rangoFechas(r.getRangoFechas())
                    .estado(r.getEstado())
                    .detalles(r.getDetalles())
                    .montoTotalEstimado(r.getMontoTotalEstimado())
                    .depositoGarantia(r.getDepositoGarantia())
                    .notasCliente(r.getNotasCliente())
                    .build();
        });

        Reserva resultado = reservaService.crearReserva(commandValido);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getIdReserva());
        assertEquals(EstadoReserva.SOLICITADA, resultado.getEstado());
        assertTrue(resultado.getCodigoReserva().startsWith("RES-"));
        assertEquals(new BigDecimal("225000.00"), resultado.getMontoTotalEstimado()); // 75000 * 3 días
        assertEquals(new BigDecimal("67500.000"), resultado.getDepositoGarantia()); // 30% de 225000
        verify(reservaRepositoryPort).guardar(any(Reserva.class));
        verify(auditoriaReservaPort).registrarCambioEstado(eq(100L), isNull(), eq("SOLICITADA"), eq(8L), anyString());
    }

    @Test
    @DisplayName("Debe rechazar la reserva si existe conflicto de disponibilidad en las fechas solicitadas")
    void crearReserva_ConflictoDisponibilidad_LanzaExcepcion() {
        when(reservaRepositoryPort.existeConflictoFechas(any(), any(), any())).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () ->
                reservaService.crearReserva(commandValido)
        );

        assertTrue(ex.getMessage().contains("ya tiene una reserva activa"));
        verify(reservaRepositoryPort, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe aprobar una reserva en estado SOLICITADA y registrar auditoría")
    void aprobarReserva_Exito() {
        Reserva existente = Reserva.builder()
                .idReserva(1L)
                .estado(EstadoReserva.SOLICITADA)
                .rangoFechas(new RangoFechas(LocalDate.now(), LocalDate.now().plusDays(2)))
                .build();

        when(reservaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(reservaRepositoryPort.guardar(any(Reserva.class))).thenReturn(existente);

        Reserva resultado = reservaService.aprobarReserva(1L, 2L);

        assertEquals(EstadoReserva.APROBADA, resultado.getEstado());
        verify(auditoriaReservaPort).registrarCambioEstado(eq(1L), eq("SOLICITADA"), eq("APROBADA"), eq(2L), anyString());
    }

    @Test
    @DisplayName("Debe rechazar una reserva guardando el motivo de rechazo")
    void rechazarReserva_Exito() {
        Reserva existente = Reserva.builder()
                .idReserva(2L)
                .estado(EstadoReserva.SOLICITADA)
                .rangoFechas(new RangoFechas(LocalDate.now(), LocalDate.now().plusDays(2)))
                .build();

        when(reservaRepositoryPort.buscarPorId(2L)).thenReturn(Optional.of(existente));
        when(reservaRepositoryPort.guardar(any(Reserva.class))).thenReturn(existente);

        Reserva resultado = reservaService.rechazarReserva(2L, "Falta de seguro de obra", 1L);

        assertEquals(EstadoReserva.RECHAZADA, resultado.getEstado());
        assertEquals("Falta de seguro de obra", resultado.getMotivoRechazo());
        verify(auditoriaReservaPort).registrarCambioEstado(eq(2L), eq("SOLICITADA"), eq("RECHAZADA"), eq(1L), anyString());
    }
}
