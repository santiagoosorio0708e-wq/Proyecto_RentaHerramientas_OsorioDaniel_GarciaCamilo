package com.rentaherramientas.modules.reportes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Modelo de Dominio Analítico: Métricas Operativas de Flota y Maquinaria.
 * Consolida indicadores de disponibilidad, mantenimiento, siniestralidad y eficiencia de retorno.
 *
 * @author Daniel Osorio (DEV 2)
 */
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

    /**
     * Calcula la tasa de utilización porcentual de la flota activa de herramientas.
     *
     * @return Porcentaje de herramientas alquiladas respecto al total registrado.
     */
    public Double calcularTasaUtilizacion() {
        if (totalHerramientas == null || totalHerramientas == 0L || herramientasAlquiladas == null) {
            return 0.0;
        }
        return (herramientasAlquiladas.doubleValue() / totalHerramientas.doubleValue()) * 100.0;
    }
}
