package com.rentaherramientas.modules.pagos.infrastructure.adapters.out.persistence.entity;

import com.rentaherramientas.modules.pagos.domain.model.EstadoFactura;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facturas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Long idFactura;

    @Column(name = "numero_factura", nullable = false, unique = true, length = 40)
    private String numeroFactura;

    @Column(name = "id_pago", nullable = false, unique = true)
    private Long idPago;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "iva", nullable = false, precision = 12, scale = 2)
    private BigDecimal iva;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_factura", nullable = false, length = 30)
    private EstadoFactura estado;

    @Column(name = "url_comprobante_pdf")
    private String urlComprobantePdf;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DetalleFacturaJpaEntity> detalles = new ArrayList<>();
}
