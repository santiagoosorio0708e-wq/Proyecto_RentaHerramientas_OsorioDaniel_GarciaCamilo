package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Campos editables de una herramienta ya registrada. */
@Schema(description = "Datos editables de una herramienta")
public record ActualizarHerramientaRequest(

        @Schema(description = "Nombre comercial", example = "Martillo demoledor electrico 1500W")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String nombre,

        @Schema(description = "Descripcion detallada")
        String descripcion,

        @Schema(description = "Marca", example = "Bosch")
        @Size(max = 60, message = "La marca no puede superar 60 caracteres")
        String marca,

        @Schema(description = "Modelo", example = "GSH 11E")
        @Size(max = 60, message = "El modelo no puede superar 60 caracteres")
        String modelo,

        @Schema(description = "Nueva tarifa diaria", example = "135000")
        @Positive(message = "La tarifa diaria debe ser mayor que cero")
        BigDecimal tarifaDiaria,

        @Schema(description = "Nuevo valor de reposicion", example = "3600000")
        @Positive(message = "El valor de reposicion debe ser mayor que cero")
        BigDecimal valorReposicion,

        @Schema(description = "Ubicacion en bodega", example = "Pasillo C - Estante 1")
        @Size(max = 120, message = "La ubicacion no puede superar 120 caracteres")
        String ubicacionBodega,

        @Schema(description = "Nueva categoria principal", example = "2")
        Long idCategoriaPrincipal
) {
}
