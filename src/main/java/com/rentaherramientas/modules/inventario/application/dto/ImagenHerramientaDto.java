package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** Vista publica de una imagen de la galeria. */
@Builder
@Schema(description = "Imagen de la galeria de una herramienta")
public record ImagenHerramientaDto(

        @Schema(description = "Identificador", example = "10")
        Long idImagen,

        @Schema(description = "URL de la foto")
        String urlFoto,

        @Schema(description = "Texto alternativo")
        String descripcion,

        @Schema(description = "Indica si es la portada", example = "true")
        boolean principal,

        @Schema(description = "Orden de aparicion", example = "1")
        Integer orden
) {
}
