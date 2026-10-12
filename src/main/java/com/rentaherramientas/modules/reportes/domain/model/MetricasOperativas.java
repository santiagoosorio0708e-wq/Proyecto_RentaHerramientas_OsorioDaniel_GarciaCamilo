package com.rentaherramientas.modules.reportes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricasOperativas {
    private Long totalHerramientas;
    private Long herramientasDisponibles;
    private Long herramientasAlquiladas;
    private Long herramientasEnMantenimiento;
    private Long herramientasFueraServicio;
    private Long totalReservas;
    private Long reservasFinalizadas;
    private Long totalDevoluciones;
    private Long devolucionesConRetraso;
    private Long totalReportesDano;
    private Double tasaIncidenciasDanoPorcentaje;
}
