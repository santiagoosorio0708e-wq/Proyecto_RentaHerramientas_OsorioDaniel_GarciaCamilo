package com.rentaherramientas.modules.identidad.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Credenciales enviadas al endpoint POST /auth/login. */
@Schema(description = "Credenciales de inicio de sesion")
public record LoginRequest(

        @Schema(description = "Correo registrado", example = "admin.perez@rentatool.com")
        @NotBlank(message = "El correo electronico es obligatorio")
        @Email(message = "El formato del correo electronico no es valido")
        String email,

        @Schema(description = "Contrasena en texto plano (viaja cifrada por HTTPS)", example = "Password123")
        @NotBlank(message = "La contrasena es obligatoria")
        String password
) {
}
