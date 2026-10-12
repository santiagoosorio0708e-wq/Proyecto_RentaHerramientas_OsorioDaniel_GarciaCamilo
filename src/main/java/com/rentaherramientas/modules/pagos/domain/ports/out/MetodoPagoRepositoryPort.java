package com.rentaherramientas.modules.pagos.domain.ports.out;

import com.rentaherramientas.modules.pagos.domain.model.MetodoPago;

import java.util.List;
import java.util.Optional;

public interface MetodoPagoRepositoryPort {
    List<MetodoPago> listarActivos();
    Optional<MetodoPago> buscarPorId(Long id);
}
