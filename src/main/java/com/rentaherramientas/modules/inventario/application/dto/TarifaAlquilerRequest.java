package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Tarifa adicional de la herramienta. */
@Schema(description = "Tarifa de alquiler segun modalidad de cobro")
public record TarifaAlquilerRequest(

        @Schema(description = "Modalidad: DIARIA, FIN_DE_SEMANA, SEMANAL o MENSUAL", example = "SEMANAL")
        @NotBlank(message = "El tipo de tarifa es obligatorio")
        @Pattern(regexp = "DIARIA|FIN_DE_SEMANA|SEMANAL|MENSUAL",
                message = "El tipo de tarifa debe ser DIARIA, FIN_DE_SEMANA, SEMANAL o MENSUAL")
        String tipoTarifa,

        @Schema(description = "Valor de la tarifa en pesos", example = "700000")
        @NotNull(message = "El valor de la tarifa es obligatorio")
        @Positive(message = "El valor de la tarifa debe ser mayor que cero")
        BigDecimal valor
) {
}
