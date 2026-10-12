package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos para crear o actualizar una categoria del catalogo. */
@Schema(description = "Datos de una categoria del catalogo")
public record CategoriaRequest(

        @Schema(description = "Nombre de la categoria", example = "Demolicion")
        @NotBlank(message = "El nombre de la categoria es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
        String nombre,

        @Schema(description = "Descripcion de la categoria", example = "Martillos y equipos de demolicion")
        @Size(max = 255, message = "La descripcion no puede superar 255 caracteres")
        String descripcion
) {
}
