package com.rentaherramientas.modules.identidad.infrastructure.security;

import com.rentaherramientas.modules.identidad.domain.model.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Puente entre el Usuario del dominio y lo que Spring Security espera recibir.
 *
 * Se exponen dos clases de authorities:
 *  - el rol completo (ROLE_ADMIN), para usar hasRole("ADMIN");
 *  - cada permiso del rol (HERRAMIENTA_CREAR), para usar hasAuthority("...").
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final transient Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        usuario.getRoles().forEach(rol -> {
            authorities.add(new SimpleGrantedAuthority(rol.getNombre()));
            if (rol.getPermisos() != null) {
                rol.getPermisos().forEach(permiso ->
                        authorities.add(new SimpleGrantedAuthority(permiso.getCodigo())));
            }
        });

        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getPassword().hash();
    }

    /** En esta plataforma el "username" es el correo electronico. */
    @Override
    public String getUsername() {
        return usuario.getEmail().valor();
    }

    public Long getIdUsuario() {
        return usuario.getIdUsuario();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuario.isActivo();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.isActivo();
    }
}
