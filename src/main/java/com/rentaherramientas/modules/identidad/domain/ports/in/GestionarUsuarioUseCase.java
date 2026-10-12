package com.rentaherramientas.modules.identidad.domain.ports.in;

import com.rentaherramientas.modules.identidad.application.dto.ActualizarPerfilRequest;
import com.rentaherramientas.modules.identidad.domain.model.Usuario;

import java.util.List;

/**
 * Puerto de entrada: consultas y administracion de usuarios.
 * Alimenta el panel del administrador y la pantalla "mi perfil".
 */
public interface GestionarUsuarioUseCase {

    Usuario obtenerPorId(Long idUsuario);

    Usuario obtenerPorEmail(String email);

    List<Usuario> listarTodos();

    List<Usuario> listarPorRol(String nombreRol);

    Usuario actualizarPerfil(String emailAutenticado, ActualizarPerfilRequest request);

    void cambiarPassword(String emailAutenticado, String passwordActual, String passwordNueva);

    /** Bloqueo y activacion de cuentas desde el panel del administrador. */
    Usuario bloquearUsuario(Long idUsuario);

    Usuario activarUsuario(Long idUsuario);

    /** Verificacion documental de un proveedor por parte del administrador. */
    Usuario verificarProveedor(Long idUsuario);
}
