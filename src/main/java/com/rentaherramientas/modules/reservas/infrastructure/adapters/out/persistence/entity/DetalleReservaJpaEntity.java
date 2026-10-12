package com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalles_reserva")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleReservaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_reserva")
    private Long idDetalleReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private ReservaJpaEntity reserva;

    @Column(name = "id_herramienta", nullable = false)
    private Long idHerramienta;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario_dia", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitarioDia;

    @Column(name = "dias_alquiler", nullable = false)
    private Integer diasAlquiler;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}
