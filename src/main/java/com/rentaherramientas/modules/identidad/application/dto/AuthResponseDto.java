package com.rentaherramientas.modules.identidad.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Set;

/** Respuesta del login y del refresh: lo que el frontend guarda en localStorage. */
@Builder
@Schema(description = "Tokens y datos basicos del usuario autenticado")
public record AuthResponseDto(

        @Schema(description = "Token de acceso JWT que debe enviarse en el header Authorization")
        String accessToken,

        @Schema(description = "Token de refresco para obtener un nuevo access token sin volver a iniciar sesion")
        String refreshToken,

        @Schema(description = "Tipo de token, siempre Bearer", example = "Bearer")
        String tipoToken,

        @Schema(description = "Segundos de vigencia del access token", example = "86400")
        long expiraEnSegundos,

        @Schema(description = "Identificador del usuario", example = "9")
        Long idUsuario,

        @Schema(description = "Nombre completo del usuario", example = "Juan Camilo Garcia")
        String nombreCompleto,

        @Schema(description = "Correo del usuario", example = "camilo.garcia@gmail.com")
        String email,

        @Schema(description = "Roles asignados", example = "[\"ROLE_CLIENTE\"]")
        Set<String> roles
) {
}
