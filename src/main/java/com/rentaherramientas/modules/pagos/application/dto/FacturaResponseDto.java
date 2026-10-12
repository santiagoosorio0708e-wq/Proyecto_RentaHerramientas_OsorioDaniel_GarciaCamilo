package com.rentaherramientas.modules.pagos.application.dto;

import com.rentaherramientas.modules.pagos.domain.model.EstadoFactura;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponseDto {

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
    private List<DetalleFacturaDto> detalles;
}
