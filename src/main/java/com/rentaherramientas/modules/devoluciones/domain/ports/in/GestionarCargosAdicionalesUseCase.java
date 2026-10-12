package com.rentaherramientas.modules.devoluciones.domain.ports.in;

import com.rentaherramientas.modules.devoluciones.domain.model.CargoAdicional;

import java.util.List;

public interface GestionarCargosAdicionalesUseCase {
    CargoAdicional registrarCargoAdicional(Long idDevolucion, CargoAdicional cargo);
    CargoAdicional marcarCargoComoPagado(Long idCargo);
    List<CargoAdicional> listarCargosPorDevolucion(Long idDevolucion);
}
