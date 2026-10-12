package com.rentaherramientas.modules.identidad.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Campos editables del perfil del usuario autenticado. */
@Schema(description = "Datos de contacto editables por el propio usuario")
public record ActualizarPerfilRequest(

        @Schema(description = "Nombre completo", example = "Juan Camilo Garcia Rueda")
        @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
        String nombreCompleto,

        @Schema(description = "Telefono de contacto", example = "3185550109")
        @Pattern(regexp = "^[0-9+\\s-]{7,20}$", message = "El telefono tiene un formato invalido")
        String telefono,

        @Schema(description = "Direccion fisica", example = "Carrera 33 # 48-19, Bucaramanga")
        @Size(max = 180, message = "La direccion no puede superar 180 caracteres")
        String direccion
) {
}
