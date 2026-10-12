package com.rentaherramientas.modules.reservas.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de Dominio: Reserva.
 * Representa el contrato de alquiler temporal de herramientas entre un cliente y el sistema.
 * Aplica invariantes de negocio DDD para transiciones de estado, cálculo de depósitos y totales.
 *
 * @author Daniel Osorio (DEV 2)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    private Long idReserva;
    private String codigoReserva;
    private Long idCliente;
    private LocalDateTime fechaSolicitud;
    private RangoFechas rangoFechas;
    private EstadoReserva estado;
    @Builder.Default
    private List<DetalleReserva> detalles = new ArrayList<>();
    private BigDecimal montoTotalEstimado;
    private BigDecimal depositoGarantia;
    private String notasCliente;
    private String motivoRechazo;

    /**
     * Indica si la reserva se encuentra en una fase activa que compromete inventario.
     *
     * @return true si el estado es SOLICITADA, APROBADA o EN_CURSO.
     */
    public boolean estaActiva() {
        return this.estado == EstadoReserva.SOLICITADA
                || this.estado == EstadoReserva.APROBADA
                || this.estado == EstadoReserva.EN_CURSO;
    }

    /**
     * Determina si la reserva requiere cobro de depósito de garantía (mayor a cero).
     *
     * @return true si el depósito es mayor a 0.
     */
    public boolean requiereDeposito() {
        return this.depositoGarantia != null && this.depositoGarantia.compareTo(BigDecimal.ZERO) > 0;
    }

    public void agregarDetalle(DetalleReserva detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        this.detalles.add(detalle);
        calcularTotales();
    }

    public void calcularTotales() {
        BigDecimal total = BigDecimal.ZERO;
        if (detalles != null) {
            for (DetalleReserva det : detalles) {
                det.calcularSubtotal();
                if (det.getSubtotal() != null) {
                    total = total.add(det.getSubtotal());
                }
            }
        }
        this.montoTotalEstimado = total;
        // El depósito de garantía se calcula como un porcentaje base (ej. 30% del valor total del alquiler)
        this.depositoGarantia = total.multiply(BigDecimal.valueOf(0.30));
    }

    public void aprobar() {
        if (this.estado != EstadoReserva.SOLICITADA) {
            throw new BusinessRuleException("Solo se pueden aprobar reservas en estado SOLICITADA");
        }
        this.estado = EstadoReserva.APROBADA;
    }

    public void rechazar(String motivo) {
        if (this.estado != EstadoReserva.SOLICITADA) {
            throw new BusinessRuleException("Solo se pueden rechazar reservas en estado SOLICITADA");
        }
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new BusinessRuleException("Debe especificarse el motivo del rechazo de la reserva");
        }
        this.motivoRechazo = motivo;
        this.estado = EstadoReserva.RECHAZADA;
    }

    public void iniciarAlquiler() {
        if (this.estado != EstadoReserva.APROBADA) {
            throw new BusinessRuleException("Solo se puede iniciar el alquiler de una reserva APROBADA");
        }
        this.estado = EstadoReserva.EN_CURSO;
    }

    public void finalizarAlquiler() {
        if (this.estado != EstadoReserva.EN_CURSO) {
            throw new BusinessRuleException("Solo se puede finalizar una reserva EN_CURSO");
        }
        this.estado = EstadoReserva.FINALIZADA;
    }

    public void cancelar() {
        if (this.estado == EstadoReserva.EN_CURSO || this.estado == EstadoReserva.FINALIZADA) {
            throw new BusinessRuleException("No se puede cancelar una reserva que ya está en curso o finalizada");
        }
        this.estado = EstadoReserva.CANCELADA;
    }
}
