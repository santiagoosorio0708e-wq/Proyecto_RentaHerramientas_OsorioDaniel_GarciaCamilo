package com.rentaherramientas.modules.devoluciones.domain.ports.out;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DevolucionReservaPort {
    record InfoReservaDevolucion(
            Long idReserva,
            String codigoReserva,
            LocalDate fechaFinPactada,
            String estadoReserva,
            List<Long> idsHerramientas
    ) {}

    Optional<InfoReservaDevolucion> obtenerInfoReserva(Long idReserva);
    void marcarReservaComoFinalizada(Long idReserva, Long idUsuario, String observacion);
}
