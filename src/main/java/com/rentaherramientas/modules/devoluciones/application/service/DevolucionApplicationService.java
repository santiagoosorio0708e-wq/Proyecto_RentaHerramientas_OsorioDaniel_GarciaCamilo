package com.rentaherramientas.modules.devoluciones.application.service;

import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.devoluciones.application.dto.EvidenciaDanoDto;
import com.rentaherramientas.modules.devoluciones.application.dto.RegistrarDevolucionCommand;
import com.rentaherramientas.modules.devoluciones.application.dto.ReporteDanoRequestDto;
import com.rentaherramientas.modules.devoluciones.domain.model.CargoAdicional;
import com.rentaherramientas.modules.devoluciones.domain.model.Devolucion;
import com.rentaherramientas.modules.devoluciones.domain.model.EvidenciaDano;
import com.rentaherramientas.modules.devoluciones.domain.model.ReporteDano;
import com.rentaherramientas.modules.devoluciones.domain.model.ResponsableDano;
import com.rentaherramientas.modules.devoluciones.domain.model.TipoCargo;
import com.rentaherramientas.modules.devoluciones.domain.model.TipoDano;
import com.rentaherramientas.modules.devoluciones.domain.ports.in.ConsultarDevolucionUseCase;
import com.rentaherramientas.modules.devoluciones.domain.ports.in.GestionarCargosAdicionalesUseCase;
import com.rentaherramientas.modules.devoluciones.domain.ports.in.GestionarReporteDanoUseCase;
import com.rentaherramientas.modules.devoluciones.domain.ports.in.RegistrarDevolucionUseCase;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.CargoAdicionalRepositoryPort;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.DevolucionHerramientaPort;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.DevolucionRepositoryPort;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.DevolucionReservaPort;
import com.rentaherramientas.modules.devoluciones.domain.ports.out.ReporteDanoRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DevolucionApplicationService implements
        RegistrarDevolucionUseCase,
        ConsultarDevolucionUseCase,
        GestionarReporteDanoUseCase,
        GestionarCargosAdicionalesUseCase {

    private final DevolucionRepositoryPort devolucionRepository;
    private final ReporteDanoRepositoryPort reporteDanoRepository;
    private final CargoAdicionalRepositoryPort cargoAdicionalRepository;
    private final DevolucionReservaPort devolucionReservaPort;
    private final DevolucionHerramientaPort devolucionHerramientaPort;

    @Override
    @Transactional
    public Devolucion registrarDevolucion(RegistrarDevolucionCommand command) {
        log.info("Iniciando registro de devolución para reserva: {}", command.getIdReserva());

        if (devolucionRepository.buscarDevolucionPorReserva(command.getIdReserva()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un registro de devolución para la reserva: " + command.getIdReserva());
        }

        var infoReserva = devolucionReservaPort.obtenerInfoReserva(command.getIdReserva())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + command.getIdReserva()));

        Devolucion devolucion = Devolucion.builder()
                .idReserva(command.getIdReserva())
                .fechaDevolucionPactada(infoReserva.fechaFinPactada())
                .fechaDevolucionReal(command.getFechaDevolucionReal())
                .recibidoPor(command.getRecibidoPor())
                .estadoEquipoGeneral(command.getEstadoEquipoGeneral())
                .observaciones(command.getObservaciones())
                .reportesDano(new ArrayList<>())
                .cargosAdicionales(new ArrayList<>())
                .build();

        int diasRetraso = devolucion.calcularDiasRetraso();
        if (diasRetraso > 0) {
            BigDecimal tarifaMora = command.getTarifaDiariaMora() != null ? command.getTarifaDiariaMora() : new BigDecimal("25000.00");
            BigDecimal montoMora = tarifaMora.multiply(BigDecimal.valueOf(diasRetraso));

            CargoAdicional cargoMora = CargoAdicional.builder()
                    .tipoCargo(TipoCargo.MORA_RETRASO)
                    .monto(montoMora)
                    .pagado(false)
                    .fechaCargo(LocalDateTime.now())
                    .descripcion("Cargo por mora: " + diasRetraso + " días de retraso en la entrega")
                    .build();
            devolucion.agregarCargoAdicional(cargoMora);
            log.info("Cargo por retraso calculado: {} para {} días", montoMora, diasRetraso);
        }

        Set<Long> herramientasConDano = new HashSet<>();

        if (command.getReportesDano() != null && !command.getReportesDano().isEmpty()) {
            for (ReporteDanoRequestDto danoDto : command.getReportesDano()) {
                herramientasConDano.add(danoDto.getIdHerramienta());

                ReporteDano reporte = ReporteDano.builder()
                        .idHerramienta(danoDto.getIdHerramienta())
                        .tipoDano(danoDto.getTipoDano())
                        .descripcionDano(danoDto.getDescripcionDano())
                        .costoEstimadoReparacion(danoDto.getCostoEstimadoReparacion())
                        .responsableAsume(danoDto.getResponsableAsume())
                        .fechaReporte(LocalDateTime.now())
                        .evidencias(new ArrayList<>())
                        .build();

                if (danoDto.getEvidencias() != null) {
                    for (EvidenciaDanoDto evDto : danoDto.getEvidencias()) {
                        EvidenciaDano ev = EvidenciaDano.builder()
                                .urlFoto(evDto.getUrlFoto())
                                .descripcionFoto(evDto.getDescripcionFoto())
                                .fechaSubida(LocalDateTime.now())
                                .build();
                        reporte.agregarEvidencia(ev);
                    }
                }
                devolucion.agregarReporteDano(reporte);

                if (danoDto.getResponsableAsume() == ResponsableDano.CLIENTE && danoDto.getCostoEstimadoReparacion().compareTo(BigDecimal.ZERO) > 0) {
                    TipoCargo tipoCargo = (danoDto.getTipoDano() == TipoDano.IRREPARABLE)
                            ? TipoCargo.REPOSICION_EQUIPO
                            : TipoCargo.REPARACION_DANO;

                    CargoAdicional cargoDano = CargoAdicional.builder()
                            .tipoCargo(tipoCargo)
                            .monto(danoDto.getCostoEstimadoReparacion())
                            .pagado(false)
                            .fechaCargo(LocalDateTime.now())
                            .descripcion("Cargo por daño (" + danoDto.getTipoDano() + ") en herramienta ID: " + danoDto.getIdHerramienta())
                            .build();
                    devolucion.agregarCargoAdicional(cargoDano);
                }

                String nuevoEstadoHerramienta = (danoDto.getTipoDano() == TipoDano.IRREPARABLE) ? "FUERA_DE_SERVICIO" : "EN_MANTENIMIENTO";
                devolucionHerramientaPort.actualizarEstadoOperativo(danoDto.getIdHerramienta(), nuevoEstadoHerramienta,
                        "Reporte de daño en devolución de reserva: " + command.getIdReserva());
            }
        }

        if (infoReserva.idsHerramientas() != null) {
            for (Long idHerramienta : infoReserva.idsHerramientas()) {
                if (!herramientasConDano.contains(idHerramienta)) {
                    devolucionHerramientaPort.actualizarEstadoOperativo(idHerramienta, "DISPONIBLE",
                            "Devuelta en buen estado de reserva: " + command.getIdReserva());
                }
            }
        }

        devolucionReservaPort.marcarReservaComoFinalizada(command.getIdReserva(), command.getRecibidoPor(), "Reserva finalizada por devolución registrada");

        Devolucion devolucionGuardada = devolucionRepository.guardarDevolucion(devolucion);
        log.info("Devolución registrada exitosamente con ID: {}", devolucionGuardada.getId());
        return devolucionGuardada;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Devolucion> obtenerDevolucionPorId(Long id) {
        return devolucionRepository.buscarDevolucionPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Devolucion> obtenerDevolucionPorReserva(Long idReserva) {
        return devolucionRepository.buscarDevolucionPorReserva(idReserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Devolucion> listarDevoluciones() {
        return devolucionRepository.listarTodasLasDevoluciones();
    }

    @Override
    @Transactional
    public ReporteDano registrarDano(Long idDevolucion, ReporteDano reporte) {
        Devolucion devolucion = devolucionRepository.buscarDevolucionPorId(idDevolucion)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada con ID: " + idDevolucion));

        reporte.setIdDevolucion(devolucion.getId());
        reporte.setFechaReporte(LocalDateTime.now());
        ReporteDano guardado = reporteDanoRepository.guardarReporteDano(reporte);

        if (reporte.getResponsableAsume() == ResponsableDano.CLIENTE && reporte.getCostoEstimadoReparacion() != null
                && reporte.getCostoEstimadoReparacion().compareTo(BigDecimal.ZERO) > 0) {
            TipoCargo tipoCargo = (reporte.getTipoDano() == TipoDano.IRREPARABLE)
                    ? TipoCargo.REPOSICION_EQUIPO
                    : TipoCargo.REPARACION_DANO;

            CargoAdicional cargo = CargoAdicional.builder()
                    .idDevolucion(devolucion.getId())
                    .tipoCargo(tipoCargo)
                    .monto(reporte.getCostoEstimadoReparacion())
                    .pagado(false)
                    .fechaCargo(LocalDateTime.now())
                    .descripcion("Cargo por reporte adicional de daño: " + reporte.getTipoDano())
                    .build();
            cargoAdicionalRepository.guardarCargoAdicional(cargo);
        }

        String estadoHerramienta = (reporte.getTipoDano() == TipoDano.IRREPARABLE) ? "FUERA_DE_SERVICIO" : "EN_MANTENIMIENTO";
        devolucionHerramientaPort.actualizarEstadoOperativo(reporte.getIdHerramienta(), estadoHerramienta,
                "Reporte de daño posterior en devolución ID: " + idDevolucion);

        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDano> listarDanosPorDevolucion(Long idDevolucion) {
        return reporteDanoRepository.buscarDanosPorDevolucion(idDevolucion);
    }

    @Override
    @Transactional
    public CargoAdicional registrarCargoAdicional(Long idDevolucion, CargoAdicional cargo) {
        Devolucion devolucion = devolucionRepository.buscarDevolucionPorId(idDevolucion)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada con ID: " + idDevolucion));
        cargo.setIdDevolucion(devolucion.getId());
        cargo.setFechaCargo(LocalDateTime.now());
        return cargoAdicionalRepository.guardarCargoAdicional(cargo);
    }

    @Override
    @Transactional
    public CargoAdicional marcarCargoComoPagado(Long idCargo) {
        CargoAdicional cargo = cargoAdicionalRepository.buscarCargoAdicionalPorId(idCargo)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo adicional no encontrado con ID: " + idCargo));
        cargo.marcarComoPagado();
        return cargoAdicionalRepository.guardarCargoAdicional(cargo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CargoAdicional> listarCargosPorDevolucion(Long idDevolucion) {
        return cargoAdicionalRepository.buscarCargosPorDevolucion(idDevolucion);
    }
}
