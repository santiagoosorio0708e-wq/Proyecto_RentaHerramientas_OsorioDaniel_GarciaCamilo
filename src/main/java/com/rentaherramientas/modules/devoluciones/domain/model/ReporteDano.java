package com.rentaherramientas.modules.devoluciones.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de Dominio: Reporte de Daño e Incidencia Física.
 * Registra los desperfectos técnicos o mecánicos identificados durante la inspección de recepción.
 * Permite deslindar la responsabilidad civil (Cliente vs Proveedor) y calcular el coste de indemnización.
 *
 * @author Daniel Osorio (DEV 2)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDano {
    private Long id;
    private Long idDevolucion;
    private Long idHerramienta;
    private TipoDano tipoDano;
    private String descripcionDano;
    private BigDecimal costoEstimadoReparacion;
    private ResponsableDano responsableAsume;
    private LocalDateTime fechaReporte;

    @Builder.Default
    private List<EvidenciaDano> evidencias = new ArrayList<>();

    public void agregarEvidencia(EvidenciaDano evidencia) {
        if (this.evidencias == null) {
            this.evidencias = new ArrayList<>();
        }
        this.evidencias.add(evidencia);
    }

    /**
     * Verifica si la responsabilidad del daño corresponde al cliente.
     *
     * @return true si responsableAsume == ResponsableDano.CLIENTE.
     */
    public boolean asumeCliente() {
        return this.responsableAsume == ResponsableDano.CLIENTE;
    }
}

