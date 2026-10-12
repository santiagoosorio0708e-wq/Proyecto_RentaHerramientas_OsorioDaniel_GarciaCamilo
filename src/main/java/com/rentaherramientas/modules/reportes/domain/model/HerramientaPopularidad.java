package com.rentaherramientas.modules.reportes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HerramientaPopularidad {
    private Long idHerramienta;
    private String nombre;
    private String marca;
    private String categoria;
    private Long totalAlquileres;
    private BigDecimal ingresosGenerados;
}
