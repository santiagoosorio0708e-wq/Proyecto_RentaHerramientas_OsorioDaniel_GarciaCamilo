package com.rentaherramientas.modules.identidad.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/** Peticion para renovar el access token usando el refresh token. */
@Schema(description = "Solicitud de renovacion de token de acceso")
public record RefreshTokenRequest(

        @Schema(description = "Refresh token entregado en el login", example = "eyJhbGciOiJIUzI1NiJ9...")
        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken
) {
}
