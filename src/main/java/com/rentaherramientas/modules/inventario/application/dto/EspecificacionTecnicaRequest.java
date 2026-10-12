package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Par atributo/valor de la ficha tecnica. */
@Schema(description = "Especificacion tecnica de la herramienta")
public record EspecificacionTecnicaRequest(

        @Schema(description = "Nombre del atributo", example = "Potencia")
        @NotBlank(message = "El atributo es obligatorio")
        @Size(max = 80, message = "El atributo no puede superar 80 caracteres")
        String atributo,

        @Schema(description = "Valor del atributo", example = "1500")
        @NotBlank(message = "El valor es obligatorio")
        @Size(max = 120, message = "El valor no puede superar 120 caracteres")
        String valor,

        @Schema(description = "Unidad de medida", example = "W")
        @Size(max = 20, message = "La unidad no puede superar 20 caracteres")
        String unidadMedida
) {
}
