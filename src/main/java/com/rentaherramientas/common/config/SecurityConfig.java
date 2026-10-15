package com.rentaherramientas.common.config;

import com.rentaherramientas.modules.identidad.infrastructure.security.JwtAccessDeniedHandler;
import com.rentaherramientas.modules.identidad.infrastructure.security.JwtAuthenticationEntryPoint;
import com.rentaherramientas.modules.identidad.infrastructure.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuracion central de seguridad y CORS de la plataforma.
 *
 * IMPORTANTE sobre las rutas de abajo: la aplicacion tiene configurado
 * server.servlet.context-path=/api/v1, y Spring Security compara los patrones
 * DESPUES de quitar ese prefijo. Por eso los requestMatchers se escriben como
 * "/reservas/**" y no como "/api/v1/reservas/**".
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /** Origenes permitidos del frontend. Se externaliza para no quemar URLs en el codigo. */
    @Value("${app.cors.allowed-origins:http://localhost:5500,http://127.0.0.1:5500,http://localhost:5173,http://localhost:3000,http://localhost:4200}")
    private String origenesPermitidos;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Se desactiva CSRF porque la API es stateless y se autentica con
                // un token en el header, no con una cookie de sesion.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)  // 401
                        .accessDeniedHandler(jwtAccessDeniedHandler))           // 403

                .authorizeHttpRequests(auth -> auth

                        // --- Preflight de CORS: debe pasar siempre ---
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // --- Recursos estaticos del frontend web ---
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/*.html",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/favicon.ico"
                        ).permitAll()

                        // --- Documentacion publica ---
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/actuator/health"
                        ).permitAll()

                        // --- Autenticacion: tiene que ser publica ---
                        .requestMatchers("/auth/**").permitAll()

                        // --- Catalogo publico de herramientas (solo lectura) ---
                        .requestMatchers(HttpMethod.GET, "/catalogo/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categorias/**").permitAll()

                        // --- Inventario: crear y modificar herramientas ---
                        .requestMatchers(HttpMethod.POST, "/herramientas/**")
                        .hasAnyRole("PROVEEDOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/herramientas/**")
                        .hasAnyRole("PROVEEDOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/herramientas/**")
                        .hasAnyRole("PROVEEDOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/herramientas/**")
                        .hasAnyRole("PROVEEDOR", "ADMIN")

                        // --- Categorias: solo el administrador las administra ---
                        .requestMatchers(HttpMethod.POST, "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categorias/**").hasRole("ADMIN")

                        // --- Modulos a cargo del compañero (reservas, pagos,
                        //     facturacion, devoluciones y reportes).
                        //     Se dejan abiertos tal como estaban para no romper
                        //     su avance; cuando su modulo integre JWT basta con
                        //     cambiar permitAll() por el rol correspondiente. ---
                        .requestMatchers(
                                "/reservas/**",
                                "/pagos/**",
                                "/facturas/**",
                                "/devoluciones/**",
                                "/reportes/**"
                        ).permitAll()

                        // --- Todo lo demas exige token valido ---
                        .anyRequest().authenticated()
                )

                // El filtro JWT corre ANTES del filtro de usuario y contrasena,
                // para que el SecurityContext ya tenga la autenticacion lista.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt para el almacenamiento de contrasenas.
     * Nunca se guarda la contrasena en texto plano en la base de datos.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origenes = Arrays.stream(origenesPermitidos.split(","))
                .map(String::trim)
                .filter(origen -> !origen.isEmpty())
                .toList();

        // allowedOriginPatterns permite usar comodines y ademas es compatible
        // con allowCredentials, cosa que allowedOrigins("*") no soporta.
        configuration.setAllowedOriginPatterns(origenes);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        configuration.setExposedHeaders(List.of("Authorization", "Location"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
