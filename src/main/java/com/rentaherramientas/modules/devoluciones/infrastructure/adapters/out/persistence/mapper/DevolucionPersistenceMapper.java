package com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.mapper;

import com.rentaherramientas.modules.devoluciones.domain.model.CargoAdicional;
import com.rentaherramientas.modules.devoluciones.domain.model.Devolucion;
import com.rentaherramientas.modules.devoluciones.domain.model.EvidenciaDano;
import com.rentaherramientas.modules.devoluciones.domain.model.ReporteDano;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity.CargoAdicionalJpaEntity;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity.DevolucionJpaEntity;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity.EvidenciaDanoJpaEntity;
import com.rentaherramientas.modules.devoluciones.infrastructure.adapters.out.persistence.entity.ReporteDanoJpaEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public final class DevolucionPersistenceMapper {

    private DevolucionPersistenceMapper() {}

    public static EvidenciaDano toDomain(EvidenciaDanoJpaEntity entity) {
        if (entity == null) return null;
        return EvidenciaDano.builder()
                .id(entity.getIdEvidencia())
                .idReporteDano(entity.getReporteDano() != null ? entity.getReporteDano().getIdReporteDano() : null)
                .urlFoto(entity.getUrlFoto())
                .descripcionFoto(entity.getDescripcionFoto())
                .fechaSubida(entity.getFechaSubida())
                .build();
    }

    public static EvidenciaDanoJpaEntity toEntity(EvidenciaDano domain, ReporteDanoJpaEntity reporte) {
        if (domain == null) return null;
        return EvidenciaDanoJpaEntity.builder()
                .idEvidencia(domain.getId())
                .reporteDano(reporte)
                .urlFoto(domain.getUrlFoto())
                .descripcionFoto(domain.getDescripcionFoto())
                .fechaSubida(domain.getFechaSubida())
                .build();
    }

    public static ReporteDano toDomain(ReporteDanoJpaEntity entity) {
        if (entity == null) return null;
        var evidenciasDomain = entity.getEvidencias() != null
                ? entity.getEvidencias().stream().map(DevolucionPersistenceMapper::toDomain).collect(Collectors.toList())
                : new ArrayList<EvidenciaDano>();

        return ReporteDano.builder()
                .id(entity.getIdReporteDano())
                .idDevolucion(entity.getDevolucion() != null ? entity.getDevolucion().getIdDevolucion() : null)
                .idHerramienta(entity.getIdHerramienta())
                .tipoDano(entity.getTipoDano())
                .descripcionDano(entity.getDescripcionDano())
                .costoEstimadoReparacion(entity.getCostoEstimadoReparacion())
                .responsableAsume(entity.getResponsableAsume())
                .fechaReporte(entity.getFechaReporte())
                .evidencias(evidenciasDomain)
                .build();
    }

    public static ReporteDanoJpaEntity toEntity(ReporteDano domain, DevolucionJpaEntity devolucion) {
        if (domain == null) return null;
        ReporteDanoJpaEntity entity = ReporteDanoJpaEntity.builder()
                .idReporteDano(domain.getId())
                .devolucion(devolucion)
                .idHerramienta(domain.getIdHerramienta())
                .tipoDano(domain.getTipoDano())
                .descripcionDano(domain.getDescripcionDano())
                .costoEstimadoReparacion(domain.getCostoEstimadoReparacion())
                .responsableAsume(domain.getResponsableAsume())
                .fechaReporte(domain.getFechaReporte())
                .evidencias(new ArrayList<>())
                .build();

        if (domain.getEvidencias() != null) {
            domain.getEvidencias().forEach(ev -> entity.agregarEvidencia(toEntity(ev, entity)));
        }
        return entity;
    }

    public static CargoAdicional toDomain(CargoAdicionalJpaEntity entity) {
        if (entity == null) return null;
        return CargoAdicional.builder()
                .id(entity.getIdCargo())
                .idDevolucion(entity.getDevolucion() != null ? entity.getDevolucion().getIdDevolucion() : null)
                .tipoCargo(entity.getTipoCargo())
                .monto(entity.getMonto())
                .pagado(entity.getPagado())
                .fechaCargo(entity.getFechaCargo())
                .descripcion(entity.getDescripcion())
                .build();
    }

    public static CargoAdicionalJpaEntity toEntity(CargoAdicional domain, DevolucionJpaEntity devolucion) {
        if (domain == null) return null;
        return CargoAdicionalJpaEntity.builder()
                .idCargo(domain.getId())
                .devolucion(devolucion)
                .tipoCargo(domain.getTipoCargo())
                .monto(domain.getMonto())
                .pagado(domain.getPagado())
                .fechaCargo(domain.getFechaCargo())
                .descripcion(domain.getDescripcion())
                .build();
    }

    public static Devolucion toDomain(DevolucionJpaEntity entity) {
        if (entity == null) return null;
        var reportesDomain = entity.getReportesDano() != null
                ? entity.getReportesDano().stream().map(DevolucionPersistenceMapper::toDomain).collect(Collectors.toList())
                : new ArrayList<ReporteDano>();

        var cargosDomain = entity.getCargosAdicionales() != null
                ? entity.getCargosAdicionales().stream().map(DevolucionPersistenceMapper::toDomain).collect(Collectors.toList())
                : new ArrayList<CargoAdicional>();

        return Devolucion.builder()
                .id(entity.getIdDevolucion())
                .idReserva(entity.getIdReserva())
                .fechaDevolucionPactada(entity.getFechaDevolucionPactada())
                .fechaDevolucionReal(entity.getFechaDevolucionReal())
                .recibidoPor(entity.getRecibidoPor())
                .estadoEquipoGeneral(entity.getEstadoEquipoGeneral())
                .diasRetraso(entity.getDiasRetraso())
                .observaciones(entity.getObservaciones())
                .reportesDano(reportesDomain)
                .cargosAdicionales(cargosDomain)
                .build();
    }

    public static DevolucionJpaEntity toEntity(Devolucion domain) {
        if (domain == null) return null;
        DevolucionJpaEntity entity = DevolucionJpaEntity.builder()
                .idDevolucion(domain.getId())
                .idReserva(domain.getIdReserva())
                .fechaDevolucionPactada(domain.getFechaDevolucionPactada())
                .fechaDevolucionReal(domain.getFechaDevolucionReal())
                .recibidoPor(domain.getRecibidoPor())
                .estadoEquipoGeneral(domain.getEstadoEquipoGeneral())
                .diasRetraso(domain.getDiasRetraso())
                .observaciones(domain.getObservaciones())
                .reportesDano(new ArrayList<>())
                .cargosAdicionales(new ArrayList<>())
                .build();

        if (domain.getReportesDano() != null) {
            domain.getReportesDano().forEach(r -> entity.agregarReporteDano(toEntity(r, entity)));
        }

        if (domain.getCargosAdicionales() != null) {
            domain.getCargosAdicionales().forEach(c -> entity.agregarCargoAdicional(toEntity(c, entity)));
        }

        return entity;
    }
}
