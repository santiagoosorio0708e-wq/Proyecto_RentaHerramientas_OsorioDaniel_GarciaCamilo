package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/** Vista publica de una categoria. */
@Builder
@Schema(description = "Categoria del catalogo")
public record CategoriaResponseDto(

        @Schema(description = "Identificador", example = "1")
        Long idCategoria,

        @Schema(description = "Nombre", example = "Demolicion")
        String nombre,

        @Schema(description = "Descripcion", example = "Martillos y equipos de demolicion")
        String descripcion
) {
}
