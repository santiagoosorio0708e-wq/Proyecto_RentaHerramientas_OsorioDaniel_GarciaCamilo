package com.rentaherramientas.modules.pagos.domain.ports.in;

import com.rentaherramientas.modules.pagos.domain.model.Factura;

import java.util.List;

public interface ConsultarFacturaUseCase {
    Factura obtenerFacturaPorId(Long id);
    Factura obtenerFacturaPorNumero(String numeroFactura);
    Factura obtenerFacturaPorPago(Long idPago);
    Factura obtenerFacturaPorReserva(Long idReserva);
    List<Factura> listarFacturasPorCliente(Long idCliente);
}
