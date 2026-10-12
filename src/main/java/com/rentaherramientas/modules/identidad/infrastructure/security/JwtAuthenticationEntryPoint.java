package com.rentaherramientas.modules.identidad.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentaherramientas.common.dto.ApiResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Responde 401 cuando la peticion llega SIN autenticar (sin token, token
 * invalido o expirado).
 *
 * Sin esta clase Spring devolveria una pagina HTML de login, que no sirve para
 * una API REST consumida por JavaScript.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponseDto<Void> cuerpo = ApiResponseDto.error(
                "No autenticado: debe iniciar sesion y enviar el token en el header Authorization");

        objectMapper.findAndRegisterModules();
        response.getWriter().write(objectMapper.writeValueAsString(cuerpo));
    }
}
