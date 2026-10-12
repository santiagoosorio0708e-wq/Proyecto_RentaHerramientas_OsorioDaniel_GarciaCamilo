package com.rentaherramientas.modules.reservas.domain.ports.in;

import com.rentaherramientas.modules.reservas.application.dto.CrearReservaCommand;
import com.rentaherramientas.modules.reservas.domain.model.Reserva;

public interface CrearReservaUseCase {
    Reserva crearReserva(CrearReservaCommand command);
}
