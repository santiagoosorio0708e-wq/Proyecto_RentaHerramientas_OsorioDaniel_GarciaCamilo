package com.rentaherramientas.modules.identidad.infrastructure.adapters.out.security;

import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Password;
import com.rentaherramientas.modules.identidad.domain.ports.out.PasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida que implementa el cifrado de contrasenas con BCrypt.
 *
 * BCrypt incorpora una sal aleatoria en cada hash, por eso dos usuarios con la
 * misma contrasena terminan con hashes distintos y nunca se comparan con equals,
 * sino con matches().
 */
@Component
@RequiredArgsConstructor
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public Password cifrar(String textoPlano) {
        return Password.deHash(passwordEncoder.encode(textoPlano));
    }

    @Override
    public boolean coincide(String textoPlano, Password passwordAlmacenada) {
        if (textoPlano == null || passwordAlmacenada == null) {
            return false;
        }
        return passwordEncoder.matches(textoPlano, passwordAlmacenada.hash());
    }
}
