package com.rentaherramientas.modules.reservas.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechazarReservaRequest {

    @NotBlank(message = "El motivo de rechazo es obligatorio")
    private String motivo;

    private Long usuarioId;
}
