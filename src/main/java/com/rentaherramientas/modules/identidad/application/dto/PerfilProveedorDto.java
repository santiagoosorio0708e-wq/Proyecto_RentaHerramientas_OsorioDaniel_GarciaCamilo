package com.rentaherramientas.modules.identidad.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

/** Datos del perfil de proveedor expuestos por la API. */
@Builder
@Schema(description = "Perfil comercial de un proveedor")
public record PerfilProveedorDto(

        @Schema(description = "Identificador del perfil", example = "1")
        Long idPerfilProveedor,

        @Schema(description = "Razon social", example = "Maquinaria y Andamios Occidente S.A.S.")
        String razonSocial,

        @Schema(description = "Nombre comercial", example = "MaquiOccidente")
        String nombreComercial,

        @Schema(description = "Ciudad de operacion", example = "Medellin")
        String ciudadOperacion,

        @Schema(description = "Sitio web", example = "https://www.maquiocc.com")
        String sitioWeb,

        @Schema(description = "Fecha de vinculacion a la plataforma")
        LocalDate fechaVinculacion,

        @Schema(description = "Indica si el administrador ya valido su documentacion", example = "true")
        boolean verificado,

        @Schema(description = "Calificacion promedio de 1 a 5", example = "4")
        Integer calificacionPromedio
) {
}
