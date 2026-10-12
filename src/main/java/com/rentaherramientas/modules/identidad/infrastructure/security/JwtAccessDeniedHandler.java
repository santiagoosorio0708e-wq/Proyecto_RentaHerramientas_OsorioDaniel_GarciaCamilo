package com.rentaherramientas.modules.identidad.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentaherramientas.common.dto.ApiResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Responde 403 cuando el usuario SI esta autenticado pero su rol no le alcanza
 * para ese endpoint.
 *
 * La diferencia con el 401 es importante y suele preguntarse:
 *   401 = no se quien eres
 *   403 = se quien eres, pero no puedes hacer esto
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponseDto<Void> cuerpo = ApiResponseDto.error(
                "Acceso denegado: su rol no tiene permiso para realizar esta operacion");

        objectMapper.findAndRegisterModules();
        response.getWriter().write(objectMapper.writeValueAsString(cuerpo));
    }
}
