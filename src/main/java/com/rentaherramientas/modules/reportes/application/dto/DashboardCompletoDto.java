package com.rentaherramientas.modules.reportes.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardCompletoDto {
    private ResumenFinancieroDto resumenFinanciero;
    private MetricasOperativasDto metricasOperativas;
    private List<HerramientaPopularidadDto> topHerramientasPopulares;
    private List<ClienteFrecuenteDto> topClientesFrecuentes;
    private List<IngresoPorProveedorDto> ingresosPorProveedor;
}
