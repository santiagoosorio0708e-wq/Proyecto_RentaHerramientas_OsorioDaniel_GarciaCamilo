package com.rentaherramientas.modules.identidad.domain.ports.out;

import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Password;

/**
 * Puerto de salida para el cifrado de contrasenas.
 *
 * Se declara aqui para que el caso de uso no dependa directamente de
 * BCryptPasswordEncoder (una clase de Spring Security). La implementacion real
 * esta en infrastructure/adapters/out/security.
 */
public interface PasswordEncoderPort {

    /** Recibe el texto plano y devuelve el Value Object ya cifrado. */
    Password cifrar(String textoPlano);

    /** Compara el texto plano contra el hash almacenado. */
    boolean coincide(String textoPlano, Password passwordAlmacenada);
}
