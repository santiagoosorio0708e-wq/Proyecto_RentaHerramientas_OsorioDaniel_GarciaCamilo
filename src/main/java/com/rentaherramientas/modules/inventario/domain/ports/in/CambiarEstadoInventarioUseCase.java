package com.rentaherramientas.modules.inventario.domain.ports.in;

import com.rentaherramientas.modules.inventario.domain.model.EstadoHerramienta;
import com.rentaherramientas.modules.inventario.domain.model.Herramienta;

import java.util.List;

/**
 * Puerto de entrada: control de estados del inventario.
 *
 * Se separa del CRUD porque responde a una necesidad distinta: aqui no se
 * editan datos de la herramienta, se mueve su disponibilidad fisica.
 */
public interface CambiarEstadoInventarioUseCase {

    Herramienta cambiarEstado(String emailSolicitante, Long idHerramienta,
                              EstadoHerramienta nuevoEstado, String motivo);

    List<Herramienta> listarPorEstado(EstadoHerramienta estado);
}
