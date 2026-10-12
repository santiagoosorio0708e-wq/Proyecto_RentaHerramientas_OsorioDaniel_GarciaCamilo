package com.rentaherramientas.modules.reportes.application.dto;

import com.rentaherramientas.modules.reportes.domain.model.ClienteFrecuente;
import com.rentaherramientas.modules.reportes.domain.model.HerramientaPopularidad;
import com.rentaherramientas.modules.reportes.domain.model.IngresoPorProveedor;
import com.rentaherramientas.modules.reportes.domain.model.MetricasOperativas;
import com.rentaherramientas.modules.reportes.domain.model.ResumenFinanciero;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ReporteDtoMapper {

    private ReporteDtoMapper() {}

    public static ResumenFinancieroDto toDto(ResumenFinanciero domain) {
        if (domain == null) return null;
        return ResumenFinancieroDto.builder()
                .totalRecaudado(domain.getTotalRecaudado())
                .totalSubtotal(domain.getTotalSubtotal())
                .totalIva(domain.getTotalIva())
                .totalCargosMora(domain.getTotalCargosMora())
                .totalCargosDano(domain.getTotalCargosDano())
                .totalFacturasEmitidas(domain.getTotalFacturasEmitidas())
                .totalPagosExitosos(domain.getTotalPagosExitosos())
                .build();
    }

    public static HerramientaPopularidadDto toDto(HerramientaPopularidad domain) {
        if (domain == null) return null;
        return HerramientaPopularidadDto.builder()
                .idHerramienta(domain.getIdHerramienta())
                .nombre(domain.getNombre())
                .marca(domain.getMarca())
                .categoria(domain.getCategoria())
                .totalAlquileres(domain.getTotalAlquileres())
                .ingresosGenerados(domain.getIngresosGenerados())
                .build();
    }

    public static ClienteFrecuenteDto toDto(ClienteFrecuente domain) {
        if (domain == null) return null;
        return ClienteFrecuenteDto.builder()
                .idCliente(domain.getIdCliente())
                .nombreCompleto(domain.getNombreCompleto())
                .email(domain.getEmail())
                .totalReservas(domain.getTotalReservas())
                .totalGastado(domain.getTotalGastado())
                .build();
    }

    public static MetricasOperativasDto toDto(MetricasOperativas domain) {
        if (domain == null) return null;
        return MetricasOperativasDto.builder()
                .totalHerramientas(domain.getTotalHerramientas())
                .herramientasDisponibles(domain.getHerramientasDisponibles())
                .herramientasAlquiladas(domain.getHerramientasAlquiladas())
                .herramientasEnMantenimiento(domain.getHerramientasEnMantenimiento())
                .herramientasFueraServicio(domain.getHerramientasFueraServicio())
                .totalReservas(domain.getTotalReservas())
                .reservasFinalizadas(domain.getReservasFinalizadas())
                .totalDevoluciones(domain.getTotalDevoluciones())
                .devolucionesConRetraso(domain.getDevolucionesConRetraso())
                .totalReportesDano(domain.getTotalReportesDano())
                .tasaIncidenciasDanoPorcentaje(domain.getTasaIncidenciasDanoPorcentaje())
                .build();
    }

    public static IngresoPorProveedorDto toDto(IngresoPorProveedor domain) {
        if (domain == null) return null;
        return IngresoPorProveedorDto.builder()
                .idProveedor(domain.getIdProveedor())
                .nombreProveedor(domain.getNombreProveedor())
                .totalHerramientasRegistradas(domain.getTotalHerramientasRegistradas())
                .totalAlquileres(domain.getTotalAlquileres())
                .ingresosGenerados(domain.getIngresosGenerados())
                .build();
    }
}
