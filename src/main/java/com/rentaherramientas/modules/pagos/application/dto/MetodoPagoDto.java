package com.rentaherramientas.modules.pagos.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoDto {

    private Long idMetodoPago;
    private String nombre;
    private String descripcion;
    private boolean activo;
}
