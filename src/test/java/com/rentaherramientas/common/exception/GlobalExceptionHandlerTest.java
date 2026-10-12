package com.rentaherramientas.common.exception;

import com.rentaherramientas.common.dto.ApiResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Debe mapear BusinessRuleException a HTTP 422 Unprocessable Entity")
    void handleBusinessRuleException_Retorna422() {
        BusinessRuleException ex = new BusinessRuleException("Saldo de garantía insuficiente");
        ResponseEntity<ApiResponseDto<Void>> response = exceptionHandler.handleBusinessRuleException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Saldo de garantía insuficiente", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Debe mapear ResourceNotFoundException a HTTP 404 Not Found")
    void handleResourceNotFoundException_Retorna404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Reserva no encontrada");
        ResponseEntity<ApiResponseDto<Void>> response = exceptionHandler.handleResourceNotFoundException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Reserva no encontrada", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Debe mapear IllegalArgumentException a HTTP 400 Bad Request")
    void handleIllegalArgumentException_Retorna400() {
        IllegalArgumentException ex = new IllegalArgumentException("Parámetro inválido");
        ResponseEntity<ApiResponseDto<Void>> response = exceptionHandler.handleIllegalArgumentException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Parámetro inválido", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Debe mapear Exception general a HTTP 500 Internal Server Error")
    void handleGeneralException_Retorna500() {
        Exception ex = new RuntimeException("Error inesperado en BD");
        ResponseEntity<ApiResponseDto<Void>> response = exceptionHandler.handleGeneralException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Error inesperado en BD"));
    }
}
