package com.rentaherramientas.modules.inventario.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Peticion de cambio de estado del inventario. */
@Schema(description = "Cambio de estado operativo de una herramienta")
public record CambiarEstadoHerramientaRequest(

        @Schema(description = "Estado destino: DISPONIBLE, ALQUILADA, EN_MANTENIMIENTO o FUERA_DE_SERVICIO",
                example = "EN_MANTENIMIENTO")
        @NotBlank(message = "El nuevo estado es obligatorio")
        @Pattern(regexp = "DISPONIBLE|ALQUILADA|EN_MANTENIMIENTO|FUERA_DE_SERVICIO",
                message = "Estado invalido. Use DISPONIBLE, ALQUILADA, EN_MANTENIMIENTO o FUERA_DE_SERVICIO")
        String nuevoEstado,

        @Schema(description = "Motivo del cambio, queda documentado para auditoria",
                example = "Revision preventiva de 500 horas")
        @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
        String motivo
) {
}
