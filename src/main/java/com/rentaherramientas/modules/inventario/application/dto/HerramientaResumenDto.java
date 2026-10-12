package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * Version ligera para las tarjetas del catalogo.
 *
 * Se usa en los listados para no enviar la ficha tecnica completa ni toda la
 * galeria de cada herramienta, que seria mucha informacion innecesaria.
 */
@Builder
@Schema(description = "Resumen de una herramienta para listados y catalogo")
public record HerramientaResumenDto(

        @Schema(description = "Identificador", example = "1")
        Long idHerramienta,

        @Schema(description = "Nombre", example = "Martillo demoledor electrico")
        String nombre,

        @Schema(description = "Marca", example = "Bosch")
        String marca,

        @Schema(description = "Modelo", example = "GSH 11E")
        String modelo,

        @Schema(description = "Categoria principal", example = "Demolicion")
        String categoriaPrincipal,

        @Schema(description = "Estado operativo", example = "DISPONIBLE")
        String estadoOperativo,

        @Schema(description = "Indica si se puede alquilar ahora", example = "true")
        boolean disponible,

        @Schema(description = "Tarifa diaria", example = "120000.00")
        BigDecimal tarifaDiaria,

        @Schema(description = "URL de la foto de portada")
        String imagenPrincipal,

        @Schema(description = "Identificador del proveedor dueno", example = "4")
        Long idProveedor
) {
}
