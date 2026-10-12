package com.rentaherramientas.modules.devoluciones.domain.ports.out;

import com.rentaherramientas.modules.devoluciones.domain.model.Devolucion;

import java.util.List;
import java.util.Optional;

public interface DevolucionRepositoryPort {
    Devolucion guardarDevolucion(Devolucion devolucion);
    Optional<Devolucion> buscarDevolucionPorId(Long id);
    Optional<Devolucion> buscarDevolucionPorReserva(Long idReserva);
    List<Devolucion> listarTodasLasDevoluciones();
}
