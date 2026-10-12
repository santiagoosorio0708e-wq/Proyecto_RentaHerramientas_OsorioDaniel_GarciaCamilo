package com.rentaherramientas.modules.pagos.domain.ports.out;

import com.rentaherramientas.modules.pagos.domain.model.Factura;

import java.util.List;
import java.util.Optional;

public interface FacturaRepositoryPort {
    Factura guardar(Factura factura);
    Optional<Factura> buscarPorId(Long id);
    Optional<Factura> buscarPorNumero(String numeroFactura);
    Optional<Factura> buscarPorPago(Long idPago);
    List<Factura> buscarPorCliente(Long idCliente);
}
