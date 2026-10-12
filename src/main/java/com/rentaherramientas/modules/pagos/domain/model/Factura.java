package com.rentaherramientas.modules.pagos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    private Long idFactura;
    private String numeroFactura;
    private Long idPago;
    private Long idCliente;
    private LocalDateTime fechaEmision;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private EstadoFactura estado;
    private String urlComprobantePdf;
    @Builder.Default
    private List<DetalleFactura> detalles = new ArrayList<>();

    public void calcularValoresFiscales(BigDecimal totalBruto) {
        this.total = totalBruto;
        // En Colombia el IVA general es del 19%
        // Total = Subtotal * 1.19 => Subtotal = Total / 1.19
        BigDecimal factor = new BigDecimal("1.19");
        this.subtotal = totalBruto.divide(factor, 2, RoundingMode.HALF_UP);
        this.iva = totalBruto.subtract(this.subtotal);
    }
}
