package com.rentaherramientas.modules.devoluciones.domain.ports.out;

public interface DevolucionHerramientaPort {
    void actualizarEstadoOperativo(Long idHerramienta, String nuevoEstado, String motivo);
}
