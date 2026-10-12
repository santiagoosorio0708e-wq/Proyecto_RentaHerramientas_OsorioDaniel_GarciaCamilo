package com.rentaherramientas.modules.pagos.domain.ports.in;

import com.rentaherramientas.modules.pagos.domain.model.Pago;

import java.util.List;

public interface ConsultarPagoUseCase {
    Pago obtenerPagoPorId(Long id);
    Pago obtenerPagoPorTransaccion(String transaccion);
    List<Pago> listarPagosPorReserva(Long idReserva);
    List<Pago> listarTodosLosPagos();
}
