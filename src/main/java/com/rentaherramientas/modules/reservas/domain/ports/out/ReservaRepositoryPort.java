package com.rentaherramientas.modules.reservas.domain.ports.out;

import com.rentaherramientas.modules.reservas.domain.model.Reserva;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepositoryPort {
    Reserva guardar(Reserva reserva);
    Optional<Reserva> buscarPorId(Long id);
    Optional<Reserva> buscarPorCodigo(String codigo);
    List<Reserva> buscarPorCliente(Long idCliente);
    List<Reserva> buscarTodas();
    boolean existeConflictoFechas(Long idHerramienta, LocalDate inicio, LocalDate fin);
}
