package com.rentaherramientas.modules.pagos.domain.ports.out;

import com.rentaherramientas.modules.pagos.domain.model.Pago;

import java.util.List;
import java.util.Optional;

public interface PagoRepositoryPort {
    Pago guardar(Pago pago);
    Optional<Pago> buscarPorId(Long id);
    Optional<Pago> buscarPorTransaccion(String transaccion);
    List<Pago> buscarPorReserva(Long idReserva);
    List<Pago> buscarTodos();
}
