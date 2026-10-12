package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Foto que se agrega a la galeria de una herramienta. */
@Schema(description = "Imagen de la galeria de la herramienta")
public record ImagenHerramientaRequest(

        @Schema(description = "URL publica de la foto", example = "https://cdn.rentatool.com/herramientas/martillo-1.jpg")
        @NotBlank(message = "La URL de la imagen es obligatoria")
        @Size(max = 255, message = "La URL no puede superar 255 caracteres")
        String urlFoto,

        @Schema(description = "Texto alternativo de la foto", example = "Vista frontal del martillo")
        @Size(max = 200, message = "La descripcion no puede superar 200 caracteres")
        String descripcion,

        @Schema(description = "Indica si es la foto de portada", example = "true")
        Boolean principal,

        @Schema(description = "Orden de aparicion en la galeria", example = "1")
        Integer orden
) {
}
