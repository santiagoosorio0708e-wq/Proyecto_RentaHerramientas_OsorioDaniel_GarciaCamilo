package com.rentaherramientas.modules.pagos.application.dto;

import com.rentaherramientas.modules.pagos.domain.model.EstadoPago;
import com.rentaherramientas.modules.pagos.domain.model.TipoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDto {

    private Long idPago;
    private String numeroTransaccion;
    private Long idReserva;
    private Long idMetodoPago;
    private BigDecimal monto;
    private TipoPago tipoPago;
    private EstadoPago estadoPago;
    private LocalDateTime fechaPago;
    private String referenciaPasarela;
    private FacturaResponseDto factura;
}
