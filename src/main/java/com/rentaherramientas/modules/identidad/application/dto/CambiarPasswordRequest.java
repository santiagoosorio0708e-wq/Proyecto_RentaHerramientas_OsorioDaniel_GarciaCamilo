package com.rentaherramientas.modules.identidad.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Peticion de cambio de contrasena del usuario autenticado. */
@Schema(description = "Datos para cambiar la contrasena del usuario autenticado")
public record CambiarPasswordRequest(

        @Schema(description = "Contrasena actual", example = "Password123")
        @NotBlank(message = "La contrasena actual es obligatoria")
        String passwordActual,

        @Schema(description = "Nueva contrasena: minimo 8 caracteres con letras y numeros", example = "NuevaClave456")
        @NotBlank(message = "La nueva contrasena es obligatoria")
        @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
        String passwordNueva
) {
}
