package com.rentaherramientas.modules.identidad.domain.ports.in;

import com.rentaherramientas.modules.identidad.application.dto.RegistroClienteCommand;
import com.rentaherramientas.modules.identidad.application.dto.RegistroProveedorCommand;
import com.rentaherramientas.modules.identidad.domain.model.Usuario;

/**
 * Puerto de entrada: casos de uso de registro.
 *
 * El registro esta separado en cliente y proveedor porque cada uno crea un
 * perfil distinto y recibe un rol distinto, tal como lo pide el enunciado
 * ("Registro diferenciado Cliente vs Proveedor").
 */
public interface RegistrarUsuarioUseCase {

    Usuario registrarCliente(RegistroClienteCommand command);

    Usuario registrarProveedor(RegistroProveedorCommand command);
}
