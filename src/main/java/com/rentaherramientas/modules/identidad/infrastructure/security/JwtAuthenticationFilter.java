package com.rentaherramientas.modules.identidad.infrastructure.security;

import com.rentaherramientas.modules.identidad.domain.ports.out.TokenProviderPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que se ejecuta UNA VEZ por peticion y traduce el header
 * "Authorization: Bearer <token>" en una autenticacion de Spring Security.
 *
 * Flujo:
 *   1. Lee el header Authorization.
 *   2. Si no hay token Bearer, deja pasar: ya decidira SecurityFilterChain si
 *      ese endpoint era publico o no.
 *   3. Si hay token, valida la firma y la expiracion.
 *   4. Carga el usuario y lo deja en el SecurityContext.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String HEADER_AUTORIZACION = "Authorization";
    private static final String PREFIJO_BEARER = "Bearer ";

    private final TokenProviderPort tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HEADER_AUTORIZACION);

        if (header == null || !header.startsWith(PREFIJO_BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Se descartan los 7 caracteres de "Bearer ".
        String token = header.substring(PREFIJO_BEARER.length());

        try {
            if (tokenProvider.esTokenValido(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Un refresh token no sirve para consumir endpoints de negocio.
                if (tokenProvider.esRefreshToken(token)) {
                    log.debug("Se intento usar un refresh token como token de acceso");
                    filterChain.doFilter(request, response);
                    return;
                }

                String email = tokenProvider.extraerEmail(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (userDetails.isEnabled()) {
                    UsernamePasswordAuthenticationToken autenticacion =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    autenticacion.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(autenticacion);
                }
            }
        } catch (Exception ex) {
            // Nunca se corta la cadena aqui: si el token falla simplemente la
            // peticion sigue sin autenticacion y el EntryPoint respondera 401.
            log.debug("No se pudo autenticar la peticion con el token recibido: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
