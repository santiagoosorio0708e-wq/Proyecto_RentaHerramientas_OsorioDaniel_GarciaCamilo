package com.rentaherramientas.modules.devoluciones.domain.ports.in;

import com.rentaherramientas.modules.devoluciones.application.dto.RegistrarDevolucionCommand;
import com.rentaherramientas.modules.devoluciones.domain.model.Devolucion;

public interface RegistrarDevolucionUseCase {
    Devolucion registrarDevolucion(RegistrarDevolucionCommand command);
}
