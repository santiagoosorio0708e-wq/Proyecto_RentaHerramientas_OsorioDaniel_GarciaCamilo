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
public class IngresoPorProveedor {
    private Long idProveedor;
    private String nombreProveedor;
    private Long totalHerramientasRegistradas;
    private Long totalAlquileres;
    private BigDecimal ingresosGenerados;
}
