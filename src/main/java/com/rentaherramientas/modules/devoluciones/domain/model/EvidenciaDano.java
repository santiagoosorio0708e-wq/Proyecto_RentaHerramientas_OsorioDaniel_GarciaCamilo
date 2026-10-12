package com.rentaherramientas.modules.devoluciones.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad de Dominio: Evidencia Fotográfica de Daño (4FN).
 * Modela una fotografía individual de avería desacoplada del reporte de daño.
 * Cumple con la Cuarta Forma Normal (4FN) al eliminar dependencias multivaluadas
 * independientes de evidencias en la tupla del reporte.
 *
 * @author Daniel Osorio (DEV 2)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvidenciaDano {
    private Long id;
    private Long idReporteDano;
    private String urlFoto;
    private String descripcionFoto;
    private LocalDateTime fechaSubida;
}
