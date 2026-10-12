package com.rentaherramientas.modules.devoluciones.application.dto;

import com.rentaherramientas.modules.devoluciones.domain.model.CargoAdicional;
import com.rentaherramientas.modules.devoluciones.domain.model.Devolucion;
import com.rentaherramientas.modules.devoluciones.domain.model.EvidenciaDano;
import com.rentaherramientas.modules.devoluciones.domain.model.ReporteDano;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class DevolucionDtoMapper {

    private DevolucionDtoMapper() {}

    public static EvidenciaDanoDto toDto(EvidenciaDano domain) {
        if (domain == null) return null;
        return EvidenciaDanoDto.builder()
                .id(domain.getId())
                .urlFoto(domain.getUrlFoto())
                .descripcionFoto(domain.getDescripcionFoto())
                .fechaSubida(domain.getFechaSubida())
                .build();
    }

    public static EvidenciaDano toDomain(EvidenciaDanoDto dto, Long idReporteDano) {
        if (dto == null) return null;
        return EvidenciaDano.builder()
                .id(dto.getId())
                .idReporteDano(idReporteDano)
                .urlFoto(dto.getUrlFoto())
                .descripcionFoto(dto.getDescripcionFoto())
                .fechaSubida(dto.getFechaSubida())
                .build();
    }

    public static ReporteDanoResponseDto toDto(ReporteDano domain) {
        if (domain == null) return null;
        List<EvidenciaDanoDto> evidenciasDto = domain.getEvidencias() != null
                ? domain.getEvidencias().stream().map(DevolucionDtoMapper::toDto).collect(Collectors.toList())
                : Collections.emptyList();

        return ReporteDanoResponseDto.builder()
                .id(domain.getId())
                .idDevolucion(domain.getIdDevolucion())
                .idHerramienta(domain.getIdHerramienta())
                .tipoDano(domain.getTipoDano())
                .descripcionDano(domain.getDescripcionDano())
                .costoEstimadoReparacion(domain.getCostoEstimadoReparacion())
                .responsableAsume(domain.getResponsableAsume())
                .fechaReporte(domain.getFechaReporte())
                .evidencias(evidenciasDto)
                .build();
    }

    public static CargoAdicionalDto toDto(CargoAdicional domain) {
        if (domain == null) return null;
        return CargoAdicionalDto.builder()
                .id(domain.getId())
                .idDevolucion(domain.getIdDevolucion())
                .tipoCargo(domain.getTipoCargo())
                .monto(domain.getMonto())
                .pagado(domain.getPagado())
                .fechaCargo(domain.getFechaCargo())
                .descripcion(domain.getDescripcion())
                .build();
    }

    public static DevolucionResponseDto toDto(Devolucion domain) {
        if (domain == null) return null;
        List<ReporteDanoResponseDto> reportesDto = domain.getReportesDano() != null
                ? domain.getReportesDano().stream().map(DevolucionDtoMapper::toDto).collect(Collectors.toList())
                : Collections.emptyList();

        List<CargoAdicionalDto> cargosDto = domain.getCargosAdicionales() != null
                ? domain.getCargosAdicionales().stream().map(DevolucionDtoMapper::toDto).collect(Collectors.toList())
                : Collections.emptyList();

        return DevolucionResponseDto.builder()
                .id(domain.getId())
                .idReserva(domain.getIdReserva())
                .fechaDevolucionPactada(domain.getFechaDevolucionPactada())
                .fechaDevolucionReal(domain.getFechaDevolucionReal())
                .recibidoPor(domain.getRecibidoPor())
                .estadoEquipoGeneral(domain.getEstadoEquipoGeneral())
                .diasRetraso(domain.getDiasRetraso())
                .observaciones(domain.getObservaciones())
                .totalCargosAdicionales(domain.calcularTotalCargosAdicionales())
                .reportesDano(reportesDto)
                .cargosAdicionales(cargosDto)
                .build();
    }
}
