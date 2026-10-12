package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** Vista publica de una especificacion tecnica. */
@Builder
@Schema(description = "Especificacion tecnica de una herramienta")
public record EspecificacionTecnicaDto(

        @Schema(description = "Identificador", example = "5")
        Long idEspecificacion,

        @Schema(description = "Atributo", example = "Potencia")
        String atributo,

        @Schema(description = "Valor", example = "1500")
        String valor,

        @Schema(description = "Unidad de medida", example = "W")
        String unidadMedida,

        @Schema(description = "Valor listo para mostrar", example = "1500 W")
        String valorConUnidad
) {
}
