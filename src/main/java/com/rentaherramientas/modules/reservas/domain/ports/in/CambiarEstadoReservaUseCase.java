package com.rentaherramientas.modules.reservas.domain.ports.in;

import com.rentaherramientas.modules.reservas.domain.model.Reserva;

/**
 * Puerto de Entrada (Use Case): Máquina de Estados y Transiciones de Reservas.
 * Gobierna las transiciones válidas del ciclo de vida del alquiler:
 * SOLICITADA ➔ APROBADA ➔ EN_CURSO ➔ FINALIZADA (o CANCELADA / RECHAZADA),
 * registrando auditoría inmutable de cada cambio de estado.
 *
 * @author Daniel Osorio (DEV 2)
 */
public interface CambiarEstadoReservaUseCase {
    Reserva aprobarReserva(Long id, Long usuarioId);
    Reserva rechazarReserva(Long id, String motivo, Long usuarioId);
    Reserva iniciarAlquiler(Long id, Long usuarioId);
    Reserva finalizarAlquiler(Long id, Long usuarioId);
    Reserva cancelarReserva(Long id, Long usuarioId);
}

