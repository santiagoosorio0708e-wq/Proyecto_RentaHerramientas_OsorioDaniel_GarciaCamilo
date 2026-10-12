package com.rentaherramientas.modules.identidad.domain.ports.out;

import com.rentaherramientas.modules.identidad.domain.model.Rol;

import java.util.List;
import java.util.Optional;

/** Puerto de salida para consultar y guardar los roles del sistema. */
public interface RolRepositoryPort {

    Optional<Rol> buscarPorNombre(String nombre);

    Optional<Rol> buscarPorId(Long idRol);

    List<Rol> listarTodos();

    Rol guardar(Rol rol);
}
