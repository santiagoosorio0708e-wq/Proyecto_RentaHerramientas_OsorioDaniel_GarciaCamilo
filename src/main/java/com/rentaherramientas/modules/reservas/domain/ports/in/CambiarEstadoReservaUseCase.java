package com.rentaherramientas.modules.reservas.domain.ports.in;

import com.rentaherramientas.modules.reservas.domain.model.Reserva;

public interface CambiarEstadoReservaUseCase {
    Reserva aprobarReserva(Long id, Long usuarioId);
    Reserva rechazarReserva(Long id, String motivo, Long usuarioId);
    Reserva iniciarAlquiler(Long id, Long usuarioId);
    Reserva finalizarAlquiler(Long id, Long usuarioId);
    Reserva cancelarReserva(Long id, Long usuarioId);
}
