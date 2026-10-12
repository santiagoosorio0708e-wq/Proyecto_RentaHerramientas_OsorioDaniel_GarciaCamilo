package com.rentaherramientas.modules.identidad.infrastructure.adapters.out.security;

import com.rentaherramientas.modules.identidad.domain.model.Usuario;
import com.rentaherramientas.modules.identidad.domain.ports.out.TokenProviderPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Adaptador de salida que emite y valida los JWT con la libreria jjwt.
 *
 * Decisiones de seguridad aplicadas aqui:
 *  - La clave secreta NO esta escrita en el codigo, llega por variable de entorno.
 *  - Todo token lleva fecha de expiracion.
 *  - En el payload solo van datos no sensibles (correo, nombre y roles).
 *    Nunca la contrasena: el payload de un JWT se puede decodificar sin la clave.
 */
@Component
public class JwtTokenProvider implements TokenProviderPort {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TIPO = "tipo";
    private static final String CLAIM_NOMBRE = "nombre";
    private static final String CLAIM_ID = "idUsuario";
    private static final String TIPO_ACCESS = "ACCESS";
    private static final String TIPO_REFRESH = "REFRESH";

    private final SecretKey claveFirma;
    private final long expiracionAccessMs;
    private final long expiracionRefreshMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secreto,
            @Value("${jwt.expiration-ms:86400000}") long expiracionAccessMs,
            @Value("${jwt.refresh-expiration-ms:604800000}") long expiracionRefreshMs) {

        this.claveFirma = construirClave(secreto);
        this.expiracionAccessMs = expiracionAccessMs;
        this.expiracionRefreshMs = expiracionRefreshMs;
    }

    /**
     * HS256 exige una clave de al menos 256 bits (32 bytes). El secreto puede
     * venir en Base64 o como texto plano, por eso se intentan las dos formas.
     */
    private SecretKey construirClave(String secreto) {
        byte[] bytes;
        try {
            bytes = Decoders.BASE64.decode(secreto);
        } catch (Exception ex) {
            bytes = secreto.getBytes(StandardCharsets.UTF_8);
        }
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "La propiedad jwt.secret debe tener al menos 256 bits (32 bytes) para firmar con HS256");
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    @Override
    public String generarAccessToken(Usuario usuario) {
        return construirToken(usuario, TIPO_ACCESS, expiracionAccessMs);
    }

    @Override
    public String generarRefreshToken(Usuario usuario) {
        return construirToken(usuario, TIPO_REFRESH, expiracionRefreshMs);
    }

    private String construirToken(Usuario usuario, String tipo, long duracionMs) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + duracionMs);

        return Jwts.builder()
                .subject(usuario.getEmail().valor())
                .claim(CLAIM_ID, usuario.getIdUsuario() == null ? 0L : usuario.getIdUsuario())
                .claim(CLAIM_NOMBRE, usuario.getNombreCompleto())
                .claim(CLAIM_ROLES, List.copyOf(usuario.nombresDeRoles()))
                .claim(CLAIM_TIPO, tipo)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(claveFirma)
                .compact();
    }

    @Override
    public String extraerEmail(String token) {
        return leerClaims(token).getSubject();
    }

    @Override
    public boolean esTokenValido(String token) {
        try {
            leerClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.debug("Token JWT expirado: {}", ex.getMessage());
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Token JWT invalido: {}", ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean esRefreshToken(String token) {
        try {
            return TIPO_REFRESH.equals(leerClaims(token).get(CLAIM_TIPO, String.class));
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    @Override
    public long segundosDeVigenciaAccessToken() {
        return expiracionAccessMs / 1000;
    }

    /** Lista de roles guardada dentro del token, usada por el filtro de seguridad. */
    @SuppressWarnings("unchecked")
    public List<String> extraerRoles(String token) {
        Object roles = leerClaims(token).get(CLAIM_ROLES);
        return (roles instanceof List<?> lista) ? (List<String>) lista : List.of();
    }

    private Claims leerClaims(String token) {
        return Jwts.parser()
                .verifyWith(claveFirma)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
