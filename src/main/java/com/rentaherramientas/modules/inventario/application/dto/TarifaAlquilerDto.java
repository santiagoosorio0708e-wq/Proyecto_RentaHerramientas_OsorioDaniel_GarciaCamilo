package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

/** Vista publica de una tarifa. */
@Builder
@Schema(description = "Tarifa de alquiler de una herramienta")
public record TarifaAlquilerDto(

        @Schema(description = "Identificador", example = "3")
        Long idTarifa,

        @Schema(description = "Modalidad de cobro", example = "SEMANAL")
        String tipoTarifa,

        @Schema(description = "Descripcion de la modalidad", example = "Cobro por semana completa")
        String descripcionTipo,

        @Schema(description = "Valor de la tarifa", example = "700000.00")
        BigDecimal valor,

        @Schema(description = "Costo equivalente por dia", example = "100000.00")
        BigDecimal valorPorDia,

        @Schema(description = "Indica si la tarifa esta vigente", example = "true")
        boolean activa
) {
}
