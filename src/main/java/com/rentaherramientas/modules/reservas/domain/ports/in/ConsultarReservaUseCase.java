package com.rentaherramientas.modules.reservas.domain.ports.in;

import com.rentaherramientas.modules.reservas.domain.model.Reserva;

import java.util.List;

public interface ConsultarReservaUseCase {
    Reserva obtenerPorId(Long id);
    Reserva obtenerPorCodigo(String codigo);
    List<Reserva> listarPorCliente(Long idCliente);
    List<Reserva> listarTodas();
}
