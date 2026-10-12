package com.rentaherramientas.modules.devoluciones.domain.ports.out;

import com.rentaherramientas.modules.devoluciones.domain.model.CargoAdicional;

import java.util.List;
import java.util.Optional;

public interface CargoAdicionalRepositoryPort {
    CargoAdicional guardarCargoAdicional(CargoAdicional cargo);
    Optional<CargoAdicional> buscarCargoAdicionalPorId(Long id);
    List<CargoAdicional> buscarCargosPorDevolucion(Long idDevolucion);
}
