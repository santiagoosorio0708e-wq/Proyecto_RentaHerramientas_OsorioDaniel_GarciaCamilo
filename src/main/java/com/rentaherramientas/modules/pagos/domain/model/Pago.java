package com.rentaherramientas.modules.pagos.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    private Long idPago;
    private String numeroTransaccion;
    private Long idReserva;
    private Long idMetodoPago;
    private BigDecimal monto;
    private TipoPago tipoPago;
    private EstadoPago estadoPago;
    private LocalDateTime fechaPago;
    private String referenciaPasarela;
    private Factura factura;

    public void aprobar(String referenciaPasarela) {
        if (this.estadoPago == EstadoPago.APROBADO) {
            throw new BusinessRuleException("El pago ya ha sido aprobado previamente");
        }
        this.estadoPago = EstadoPago.APROBADO;
        this.referenciaPasarela = referenciaPasarela;
    }

    public void rechazar(String motivo) {
        if (this.estadoPago == EstadoPago.APROBADO) {
            throw new BusinessRuleException("No se puede rechazar un pago que ya fue aprobado");
        }
        this.estadoPago = EstadoPago.RECHAZADO;
        this.referenciaPasarela = "RECHAZADO: " + motivo;
    }

    public Factura generarFacturaAutomatica(Long idCliente, String descripcionServicio) {
        if (this.estadoPago != EstadoPago.APROBADO) {
            throw new BusinessRuleException("Solo se pueden generar facturas para pagos APROBADOS");
        }

        String numeroFactura = "FAC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Factura nuevaFactura = Factura.builder()
                .numeroFactura(numeroFactura)
                .idPago(this.idPago)
                .idCliente(idCliente)
                .fechaEmision(LocalDateTime.now())
                .estado(EstadoFactura.PAGADA)
                .urlComprobantePdf("/api/v1/facturas/" + numeroFactura + "/descargar")
                .build();

        nuevaFactura.calcularValoresFiscales(this.monto);

        DetalleFactura detalle = DetalleFactura.builder()
                .descripcionItem(descripcionServicio != null ? descripcionServicio : "Alquiler de equipos de construcción - Reserva #" + this.idReserva)
                .cantidad(1)
                .precioUnitario(nuevaFactura.getSubtotal())
                .subtotalLinea(nuevaFactura.getSubtotal())
                .build();

        nuevaFactura.getDetalles().add(detalle);
        this.factura = nuevaFactura;
        return nuevaFactura;
    }
}
