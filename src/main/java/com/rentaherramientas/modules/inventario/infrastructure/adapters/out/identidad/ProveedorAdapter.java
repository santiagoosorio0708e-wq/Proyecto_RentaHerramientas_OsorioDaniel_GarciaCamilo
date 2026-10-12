package com.rentaherramientas.modules.inventario.infrastructure.adapters.out.identidad;

import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.identidad.domain.model.Usuario;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Email;
import com.rentaherramientas.modules.identidad.domain.ports.out.UsuarioRepositoryPort;
import com.rentaherramientas.modules.inventario.domain.ports.out.ProveedorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del puerto anticorrupcion hacia Identidad.
 *
 * Esta clase es el unico punto del modulo de Inventario que conoce las clases
 * del modulo de Identidad. Si el dia de manana Identidad se convierte en un
 * microservicio aparte, solo habria que reescribir este adaptador para que
 * haga una llamada HTTP en vez de una consulta local.
 */
@Component
@RequiredArgsConstructor
public class ProveedorAdapter implements ProveedorPort {

    private final UsuarioRepositoryPort usuarioRepository;

    private Usuario buscar(String email) {
        return usuarioRepository.buscarPorEmail(new Email(email))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un usuario registrado con el correo " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public Long obtenerIdUsuarioPorEmail(String email) {
        return buscar(email).getIdUsuario();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esAdministrador(String email) {
        return buscar(email).esAdministrador();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esProveedor(String email) {
        return buscar(email).esProveedor();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaVerificado(String email) {
        Usuario usuario = buscar(email);
        return usuario.getPerfilProveedor() != null
                && usuario.getPerfilProveedor().puedePublicarHerramientas();
    }

    @Override
    @Transactional(readOnly = true)
    public String ciudadDeOperacion(Long idProveedor) {
        return usuarioRepository.buscarPorId(idProveedor)
                .filter(usuario -> usuario.getPerfilProveedor() != null)
                .map(usuario -> usuario.getPerfilProveedor().getCiudadOperacion())
                .orElse(null);
    }
}
