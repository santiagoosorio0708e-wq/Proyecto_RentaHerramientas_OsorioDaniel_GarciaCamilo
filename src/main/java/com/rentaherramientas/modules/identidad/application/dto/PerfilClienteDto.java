package com.rentaherramientas.modules.identidad.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Datos del perfil de cliente expuestos por la API. */
@Builder
@Schema(description = "Perfil de un cliente de la plataforma")
public record PerfilClienteDto(

        @Schema(description = "Identificador del perfil", example = "1")
        Long idPerfilCliente,

        @Schema(description = "PARTICULAR o EMPRESARIAL", example = "PARTICULAR")
        String tipoCliente,

        @Schema(description = "Ciudad de residencia", example = "Bucaramanga")
        String ciudadResidencia,

        @Schema(description = "Cupo de credito aprobado", example = "1500000.00")
        BigDecimal cupoCredito,

        @Schema(description = "Fecha de registro")
        LocalDate fechaRegistro,

        @Schema(description = "Cantidad de alquileres realizados", example = "3")
        Integer alquileresRealizados
) {
}
