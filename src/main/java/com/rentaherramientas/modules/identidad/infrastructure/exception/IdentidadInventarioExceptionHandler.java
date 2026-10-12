package com.rentaherramientas.modules.identidad.infrastructure.exception;

import com.rentaherramientas.common.dto.ApiResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

/**
 * Manejador de excepciones de los bounded contexts de Identidad e Inventario.
 *
 * Se declara acotado por basePackages y con prioridad alta para que atienda
 * primero los errores de estos dos modulos, sin modificar ni interferir con el
 * GlobalExceptionHandler general del proyecto, que sigue cubriendo el resto.
 *
 * El objetivo es que el frontend reciba siempre un JSON con la misma forma y un
 * codigo HTTP correcto, en vez de un 500 generico o un stack trace.
 */
@RestControllerAdvice(basePackages = {
        "com.rentaherramientas.modules.identidad",
        "com.rentaherramientas.modules.inventario"
})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IdentidadInventarioExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(IdentidadInventarioExceptionHandler.class);

    /** 401: credenciales incorrectas. */
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiResponseDto<Void>> manejarCredenciales(Exception ex) {
        log.debug("Intento de autenticacion fallido: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDto.error("Credenciales invalidas"));
    }

    /** 403: autenticado pero sin permisos suficientes. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Void>> manejarAccesoDenegado(AccessDeniedException ex) {
        log.debug("Acceso denegado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponseDto.error(
                        "Acceso denegado: su rol no tiene permiso para realizar esta operacion"));
    }

    /**
     * 400: el valor de un parametro no se pudo convertir.
     * Ejemplo tipico: /herramientas/abc cuando se espera un numero.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDto<Void>> manejarTipoInvalido(
            MethodArgumentTypeMismatchException ex) {

        String tipoEsperado = ex.getRequiredType() == null
                ? "el tipo esperado" : ex.getRequiredType().getSimpleName();

        return ResponseEntity.badRequest().body(ApiResponseDto.error(
                "El parametro '" + ex.getName() + "' no es valido. Se esperaba " + tipoEsperado + "."));
    }

    /**
     * 400: enum invalido u otro argumento ilegal.
     * Ejemplo: enviar "estado": "DANADA" cuando el enum no lo contempla.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto<Void>> manejarArgumentoIlegal(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ApiResponseDto.error(
                "Valor no permitido: " + ex.getMessage()));
    }

    /** 400: el cuerpo JSON llego mal formado o vacio. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDto<Void>> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        log.debug("JSON mal formado: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponseDto.error(
                "El cuerpo de la peticion no es un JSON valido o esta vacio"));
    }

    /** 400: falta un parametro obligatorio en la query string. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseDto<Void>> manejarParametroFaltante(
            MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(ApiResponseDto.error(
                "Falta el parametro obligatorio '" + ex.getParameterName() + "'"));
    }

    /** 405: metodo HTTP incorrecto para esa ruta. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponseDto<Void>> manejarMetodoNoSoportado(
            HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ApiResponseDto.error(
                "El metodo " + ex.getMethod() + " no esta permitido en esta ruta"));
    }

    /**
     * 409: se violo una restriccion de la base de datos (UNIQUE o FOREIGN KEY).
     *
     * Se devuelve un mensaje propio y el detalle tecnico se deja solo en el log:
     * exponer el texto original de MySQL filtraria nombres de tablas y columnas.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<Map<String, String>>> manejarIntegridad(
            DataIntegrityViolationException ex) {

        log.error("Violacion de integridad en base de datos", ex);

        ApiResponseDto<Map<String, String>> respuesta = ApiResponseDto.<Map<String, String>>builder()
                .success(false)
                .message("La operacion viola una restriccion de la base de datos. "
                        + "Revise que los datos unicos (correo, documento, numero de serie) no esten repetidos.")
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }
}
