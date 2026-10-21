package com.rentaherramientas.modules.identidad.application.service;

import com.rentaherramientas.common.exception.BusinessRuleException;
import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.identidad.application.dto.ActualizarPerfilRequest;
import com.rentaherramientas.modules.identidad.domain.model.Usuario;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Email;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Password;
import com.rentaherramientas.modules.identidad.domain.ports.in.GestionarUsuarioUseCase;
import com.rentaherramientas.modules.identidad.domain.ports.out.PasswordEncoderPort;
import com.rentaherramientas.modules.identidad.domain.ports.out.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso de consulta y administracion de usuarios.
 *
 * Es lo que alimenta la tabla de control del administrador (activar / bloquear
 * cuentas) y la pantalla de perfil del usuario autenticado.
 */
@Service
@RequiredArgsConstructor
public class UsuarioApplicationService implements GestionarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long idUsuario) {
        return usuarioRepository.buscarPorId(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el usuario con id " + idUsuario));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(new Email(email))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro el usuario con correo " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.listarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarPorRol(String nombreRol) {
        String rolNormalizado = nombreRol == null ? "" : nombreRol.trim().toUpperCase();
        if (!rolNormalizado.startsWith("ROLE_")) {
            rolNormalizado = "ROLE_" + rolNormalizado;
        }
        return usuarioRepository.listarPorRol(rolNormalizado);
    }

    @Override
    @Transactional
    public Usuario actualizarPerfil(String emailAutenticado, ActualizarPerfilRequest request) {
        Usuario usuario = obtenerPorEmail(emailAutenticado);

        if (request.email() != null && !request.email().isBlank()
                && !request.email().trim().equalsIgnoreCase(emailAutenticado.trim())) {
            if (usuarioRepository.buscarPorEmail(new Email(request.email().trim())).isPresent()) {
                throw new BusinessRuleException("El correo electronico ya esta registrado por otro usuario");
            }
        }

        usuario.actualizarDatosContacto(
                request.nombreCompleto(), request.email(), request.telefono(), request.direccion());
        return usuarioRepository.guardar(usuario);
    }

    @Override
    @Transactional
    public void cambiarPassword(String emailAutenticado, String passwordActual, String passwordNueva) {
        Usuario usuario = obtenerPorEmail(emailAutenticado);

        if (!passwordEncoder.coincide(passwordActual, usuario.getPassword())) {
            throw new BusinessRuleException("La contrasena actual no es correcta");
        }
        if (passwordActual.equals(passwordNueva)) {
            throw new BusinessRuleException("La nueva contrasena debe ser diferente de la actual");
        }

        Password.validarPoliticaTextoPlano(passwordNueva);
        usuario.cambiarPassword(passwordEncoder.cifrar(passwordNueva));
        usuarioRepository.guardar(usuario);
    }

    @Override
    @Transactional
    public Usuario bloquearUsuario(Long idUsuario) {
        Usuario usuario = obtenerPorId(idUsuario);
        usuario.bloquear();
        return usuarioRepository.guardar(usuario);
    }

    @Override
    @Transactional
    public Usuario activarUsuario(Long idUsuario) {
        Usuario usuario = obtenerPorId(idUsuario);
        usuario.activar();
        return usuarioRepository.guardar(usuario);
    }

    @Override
    @Transactional
    public Usuario verificarProveedor(Long idUsuario) {
        Usuario usuario = obtenerPorId(idUsuario);

        if (!usuario.esProveedor() || usuario.getPerfilProveedor() == null) {
            throw new BusinessRuleException(
                    "El usuario " + idUsuario + " no tiene un perfil de proveedor por verificar");
        }

        usuario.getPerfilProveedor().verificar();
        return usuarioRepository.guardar(usuario);
    }
}
