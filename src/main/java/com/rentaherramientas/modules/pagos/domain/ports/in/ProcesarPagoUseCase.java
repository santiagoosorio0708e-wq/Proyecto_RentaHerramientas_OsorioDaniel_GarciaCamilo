package com.rentaherramientas.modules.pagos.domain.ports.in;

import com.rentaherramientas.modules.pagos.application.dto.ProcesarPagoCommand;
import com.rentaherramientas.modules.pagos.domain.model.Pago;

public interface ProcesarPagoUseCase {
    Pago procesarPago(ProcesarPagoCommand command);
}
