package com.rentaherramientas.modules.reservas.infrastructure.adapters.out.persistence.entity;

import com.rentaherramientas.modules.reservas.domain.model.EstadoReserva;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(name = "codigo_reserva", nullable = false, unique = true, length = 30)
    private String codigoReserva;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoReserva estado;

    @Column(name = "monto_total_estimado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotalEstimado;

    @Column(name = "deposito_garantia", nullable = false, precision = 12, scale = 2)
    private BigDecimal depositoGarantia;

    @Column(name = "notas_cliente")
    private String notasCliente;

    @Column(name = "motivo_rechazo")
    private String motivoRechazo;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DetalleReservaJpaEntity> detalles = new ArrayList<>();
}
