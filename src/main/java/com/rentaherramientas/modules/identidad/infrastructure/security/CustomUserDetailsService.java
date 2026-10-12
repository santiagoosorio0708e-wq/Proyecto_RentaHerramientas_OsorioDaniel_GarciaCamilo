package com.rentaherramientas.modules.identidad.infrastructure.security;

import com.rentaherramientas.modules.identidad.domain.model.Usuario;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Email;
import com.rentaherramientas.modules.identidad.domain.ports.out.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga el usuario desde la base de datos para que Spring Security pueda
 * construir el objeto Authentication en cada peticion.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepositoryPort usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.buscarPorEmail(new Email(email))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe un usuario registrado con el correo " + email));
        return new CustomUserDetails(usuario);
    }
}
