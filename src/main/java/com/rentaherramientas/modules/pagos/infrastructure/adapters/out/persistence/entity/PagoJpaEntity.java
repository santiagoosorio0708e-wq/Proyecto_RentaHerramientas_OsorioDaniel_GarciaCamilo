package com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity;

import com.rentaherramientas.modules.pagos.domain.model.EstadoPago;
import com.rentaherramientas.modules.pagos.domain.model.TipoPago;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @Column(name = "numero_transaccion", nullable = false, unique = true, length = 60)
    private String numeroTransaccion;

    @Column(name = "id_reserva", nullable = false)
    private Long idReserva;

    @Column(name = "id_metodo_pago", nullable = false)
    private Long idMetodoPago;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pago", nullable = false, length = 30)
    private TipoPago tipoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false, length = 30)
    private EstadoPago estadoPago;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "referencia_pasarela", length = 120)
    private String referenciaPasarela;
}
