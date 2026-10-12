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

/**
 * Entidad de Dominio: Factura Electrónica.
 * Emite el comprobante fiscal asociado al cobro de una reserva o liquidación de alquiler.
 * Desglosa la Base Gravable (Subtotal) y el Impuesto al Valor Agregado (IVA 19%) según
 * el Artículo 468 del Estatuto Tributario de Colombia.
 *
 * @author Daniel Osorio (DEV 2)
 */
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

    /**
     * Calcula la base gravable y el valor del IVA del 19% a partir del total facturado.
     * Aplica redondeo bancario HALF_UP a 2 decimales para precisión contable.
     *
     * @param totalBruto Importe total recaudado.
     */
    public void calcularValoresFiscales(BigDecimal totalBruto) {
        this.total = totalBruto;
        // En Colombia el IVA general es del 19%
        // Total = Subtotal * 1.19 => Subtotal = Total / 1.19
        BigDecimal factor = new BigDecimal("1.19");
        this.subtotal = totalBruto.divide(factor, 2, RoundingMode.HALF_UP);
        this.iva = totalBruto.subtract(this.subtotal);
    }

    /**
     * Verifica si la factura se encuentra en estado formalmente PAGADA.
     *
     * @return true si el estado es PAGADA.
     */
    public boolean estaPagada() {
        return this.estado == EstadoFactura.PAGADA;
    }
}
