package com.rentaherramientas.modules.devoluciones.application.service;

import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.devoluciones.application.dto.EvidenciaDanoDto;
import com.rentaherramientas.modules.devoluciones.application.dto.RegistrarDevolucionCommand;
import com.rentaherramientas.modules.devoluciones.application.dto.ReporteDanoRequestDto;
import com.rentaherramientas.modules.devoluciones.domain.model.CargoAdicional;
import com.rentaherramientas.modules.devoluciones.domain.model.Devolucion;
import com.rentaherramientas.modules.devoluciones.domain.model.EstadoEquipoGeneral;
import com.rentaherramientas.modules.devoluciones.domain.model.ResponsableDano;
import com.rentaherramientas.modules.devoluciones.domain.model.TipoCargo;
import com.rentaherramientas.modules.devoluciones.domain.model.TipoDano;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.CargoAdicionalRepositoryPort;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.DevolucionHerramientaPort;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.DevolucionRepositoryPort;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.DevolucionReservaPort;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.ReporteDanoRepositoryPort;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevolucionApplicationServiceTest {

    @Mock
    private DevolucionRepositoryPort devolucionRepository;

    @Mock
    private ReporteDanoRepositoryPort reporteDanoRepository;

    @Mock
    private CargoAdicionalRepositoryPort cargoAdicionalRepository;

    @Mock
    private DevolucionReservaPort devolucionReservaPort;

    @Mock
    private DevolucionHerramientaPort devolucionHerramientaPort;

    @InjectMocks
    private DevolucionApplicationService devolucionService;

    private Long idReserva;
    private Long idUsuario;

    @BeforeEach
    void setUp() {
        idReserva = 100L;
        idUsuario = 2L;
    }

    @Test
    @DisplayName("Debe registrar devolución a tiempo sin daños y cambiar estado de herramientas a DISPONIBLE")
    void testRegistrarDevolucionATiempoSinDanos() {
        LocalDate fechaPactada = LocalDate.of(2026, 10, 15);
        LocalDate fechaReal = LocalDate.of(2026, 10, 15);

        when(devolucionRepository.buscarDevolucionPorReserva(idReserva)).thenReturn(Optional.empty());
        when(devolucionReservaPort.obtenerInfoReserva(idReserva)).thenReturn(Optional.of(
                new DevolucionReservaPort.InfoReservaDevolucion(idReserva, "RES-2026-001", fechaPactada, "EN_CURSO", List.of(10L, 11L))
        ));
        when(devolucionRepository.guardarDevolucion(any(Devolucion.class))).thenAnswer(i -> {
            Devolucion d = i.getArgument(0);
            d.setId(1L);
            return d;
        });

        RegistrarDevolucionCommand command = RegistrarDevolucionCommand.builder()
                .idReserva(idReserva)
                .fechaDevolucionReal(fechaReal)
                .recibidoPor(idUsuario)
                .estadoEquipoGeneral(EstadoEquipoGeneral.BUEN_ESTADO)
                .observaciones("Todo perfecto")
                .reportesDano(List.of())
                .build();

        Devolucion resultado = devolucionService.registrarDevolucion(command);

        assertNotNull(resultado);
        assertEquals(0, resultado.getDiasRetraso());
        assertTrue(resultado.getCargosAdicionales().isEmpty());
        verify(devolucionHerramientaPort).actualizarEstadoOperativo(10L, "DISPONIBLE", "Devuelta en buen estado de reserva: 100");
        verify(devolucionHerramientaPort).actualizarEstadoOperativo(11L, "DISPONIBLE", "Devuelta en buen estado de reserva: 100");
        verify(devolucionReservaPort).marcarReservaComoFinalizada(eq(idReserva), eq(idUsuario), anyString());
    }

    @Test
    @DisplayName("Debe registrar devolución con retraso y generar cargo adicional por mora")
    void testRegistrarDevolucionConRetrasoGeneraCargoMora() {
        LocalDate fechaPactada = LocalDate.of(2026, 10, 15);
        LocalDate fechaReal = LocalDate.of(2026, 10, 18); // 3 días de retraso

        when(devolucionRepository.buscarDevolucionPorReserva(idReserva)).thenReturn(Optional.empty());
        when(devolucionReservaPort.obtenerInfoReserva(idReserva)).thenReturn(Optional.of(
                new DevolucionReservaPort.InfoReservaDevolucion(idReserva, "RES-2026-002", fechaPactada, "EN_CURSO", List.of(10L))
        ));
        when(devolucionRepository.guardarDevolucion(any(Devolucion.class))).thenAnswer(i -> i.getArgument(0));

        RegistrarDevolucionCommand command = RegistrarDevolucionCommand.builder()
                .idReserva(idReserva)
                .fechaDevolucionReal(fechaReal)
                .recibidoPor(idUsuario)
                .estadoEquipoGeneral(EstadoEquipoGeneral.BUEN_ESTADO)
                .tarifaDiariaMora(new BigDecimal("30000.00"))
                .observaciones("Entrega demorada por tráfico en obra")
                .build();

        Devolucion resultado = devolucionService.registrarDevolucion(command);

        assertNotNull(resultado);
        assertEquals(3, resultado.getDiasRetraso());
        assertEquals(1, resultado.getCargosAdicionales().size());

        CargoAdicional cargo = resultado.getCargosAdicionales().get(0);
        assertEquals(TipoCargo.MORA_RETRASO, cargo.getTipoCargo());
        assertEquals(new BigDecimal("90000.00"), cargo.getMonto());
        assertFalse(cargo.getPagado());
    }

    @Test
    @DisplayName("Debe registrar devolución con daño, generar cargo de reparación y cambiar herramienta a EN_MANTENIMIENTO")
    void testRegistrarDevolucionConDanos() {
        LocalDate fechaPactada = LocalDate.of(2026, 10, 15);
        LocalDate fechaReal = LocalDate.of(2026, 10, 15);

        when(devolucionRepository.buscarDevolucionPorReserva(idReserva)).thenReturn(Optional.empty());
        when(devolucionReservaPort.obtenerInfoReserva(idReserva)).thenReturn(Optional.of(
                new DevolucionReservaPort.InfoReservaDevolucion(idReserva, "RES-2026-003", fechaPactada, "EN_CURSO", List.of(20L))
        ));
        when(devolucionRepository.guardarDevolucion(any(Devolucion.class))).thenAnswer(i -> i.getArgument(0));

        ReporteDanoRequestDto danoDto = ReporteDanoRequestDto.builder()
                .idHerramienta(20L)
                .tipoDano(TipoDano.MECANICO)
                .descripcionDano("Engranaje trabado y sobrecalentamiento de bobina")
                .costoEstimadoReparacion(new BigDecimal("150000.00"))
                .responsableAsume(ResponsableDano.CLIENTE)
                .evidencias(List.of(
                        EvidenciaDanoDto.builder()
                                .urlFoto("https://cloudinary.com/foto-engranaje.jpg")
                                .descripcionFoto("Foto del daño en engranaje principal")
                                .build()
                ))
                .build();

        RegistrarDevolucionCommand command = RegistrarDevolucionCommand.builder()
                .idReserva(idReserva)
                .fechaDevolucionReal(fechaReal)
                .recibidoPor(idUsuario)
                .estadoEquipoGeneral(EstadoEquipoGeneral.DANO_LEVE)
                .reportesDano(List.of(danoDto))
                .build();

        Devolucion resultado = devolucionService.registrarDevolucion(command);

        assertNotNull(resultado);
        assertEquals(1, resultado.getReportesDano().size());
        assertEquals(1, resultado.getCargosAdicionales().size());

        CargoAdicional cargo = resultado.getCargosAdicionales().get(0);
        assertEquals(TipoCargo.REPARACION_DANO, cargo.getTipoCargo());
        assertEquals(new BigDecimal("150000.00"), cargo.getMonto());

        verify(devolucionHerramientaPort).actualizarEstadoOperativo(eq(20L), eq("EN_MANTENIMIENTO"), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existe una devolución registrada para la reserva")
    void testRegistrarDevolucionDuplicadaLanzaExcepcion() {
        when(devolucionRepository.buscarDevolucionPorReserva(idReserva))
                .thenReturn(Optional.of(Devolucion.builder().id(5L).build()));

        RegistrarDevolucionCommand command = RegistrarDevolucionCommand.builder()
                .idReserva(idReserva)
                .fechaDevolucionReal(LocalDate.now())
                .recibidoPor(idUsuario)
                .estadoEquipoGeneral(EstadoEquipoGeneral.BUEN_ESTADO)
                .build();

        assertThrows(IllegalArgumentException.class, () -> devolucionService.registrarDevolucion(command));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si se intenta registrar daño para devolución inexistente")
    void testRegistrarDanoDevolucionNoEncontradaLanzaExcepcion() {
        when(devolucionRepository.buscarDevolucionPorId(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                devolucionService.registrarDano(999L, com.rentaherramientas.modules.devoluciones.domain.model.ReporteDano.builder().build())
        );
    }
}

