package com.rentaherramientas.modules.devoluciones.domain.ports.in;

import com.rentaherramientas.modules.devoluciones.domain.model.Devolucion;

import java.util.List;
import java.util.Optional;

public interface ConsultarDevolucionUseCase {
    Optional<Devolucion> obtenerDevolucionPorId(Long id);
    Optional<Devolucion> obtenerDevolucionPorReserva(Long idReserva);
    List<Devolucion> listarDevoluciones();
}
