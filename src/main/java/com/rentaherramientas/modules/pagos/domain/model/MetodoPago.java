package com.rentaherramientas.modules.pagos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPago {

    private Long idMetodoPago;
    private String nombre;
    private String descripcion;
    private boolean activo;
}
