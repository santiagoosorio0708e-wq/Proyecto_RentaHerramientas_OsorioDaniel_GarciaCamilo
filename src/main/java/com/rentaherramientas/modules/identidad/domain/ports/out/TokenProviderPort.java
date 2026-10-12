package com.rentaherramientas.modules.identidad.domain.ports.out;

import com.rentaherramientas.modules.identidad.domain.model.Usuario;

/**
 * Puerto de salida para la generacion y lectura de tokens de acceso.
 *
 * El dominio solo sabe que "existe algo que emite un token"; que ese algo sea
 * JWT firmado con HS256 es un detalle de infraestructura.
 */
public interface TokenProviderPort {

    /** Token de acceso de corta duracion que viaja en cada peticion. */
    String generarAccessToken(Usuario usuario);

    /** Token de larga duracion usado unicamente para pedir un nuevo access token. */
    String generarRefreshToken(Usuario usuario);

    /** Email (subject) contenido en el token. */
    String extraerEmail(String token);

    boolean esTokenValido(String token);

    /** True si el token fue emitido como refresh token y no como access token. */
    boolean esRefreshToken(String token);

    /** Segundos que faltan para que expire el access token, para informarlo al frontend. */
    long segundosDeVigenciaAccessToken();
}
