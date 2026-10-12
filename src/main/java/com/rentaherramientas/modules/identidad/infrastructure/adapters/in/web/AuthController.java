package com.rentaherramientas.modules.identidad.infrastructure.adapters.in.web;

import com.rentaherramientas.common.dto.ApiResponseDto;
import com.rentaherramientas.modules.identidad.application.dto.AuthResponseDto;
import com.rentaherramientas.modules.identidad.application.dto.LoginRequest;
import com.rentaherramientas.modules.identidad.application.dto.RefreshTokenRequest;
import com.rentaherramientas.modules.identidad.application.dto.RegistroClienteCommand;
import com.rentaherramientas.modules.identidad.application.dto.RegistroProveedorCommand;
import com.rentaherramientas.modules.identidad.application.dto.UsuarioDtoMapper;
import com.rentaherramientas.modules.identidad.application.dto.UsuarioResponseDto;
import com.rentaherramientas.modules.identidad.domain.model.Usuario;
import com.rentaherramientas.modules.identidad.domain.ports.in.AutenticarUsuarioUseCase;
import com.rentaherramientas.modules.identidad.domain.ports.in.RegistrarUsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Adaptador de entrada REST para autenticacion.
 *
 * Todos los endpoints de este controlador son publicos: si el login exigiera
 * token, seria imposible iniciar sesion por primera vez.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@SecurityRequirements
@Tag(name = "1. Autenticacion",
        description = "Registro de clientes y proveedores, inicio de sesion y renovacion de token JWT")
public class AuthController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @PostMapping("/registro/cliente")
    @Operation(summary = "Registrar un nuevo cliente",
            description = "Crea la cuenta, le asigna el rol ROLE_CLIENTE y genera su perfil de cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "422", description = "El correo o el documento ya estan registrados")
    })
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> registrarCliente(
            @Valid @RequestBody RegistroClienteCommand command) {

        Usuario usuario = registrarUsuarioUseCase.registrarCliente(command);
        UsuarioResponseDto dto = UsuarioDtoMapper.toResponseDto(usuario);

        return ResponseEntity
                .created(URI.create("/api/v1/usuarios/" + usuario.getIdUsuario()))
                .body(ApiResponseDto.ok("Cliente registrado con exito", dto));
    }

    @PostMapping("/registro/proveedor")
    @Operation(summary = "Registrar un nuevo proveedor",
            description = "Crea la cuenta con rol ROLE_PROVEEDOR. Queda pendiente de verificacion "
                    + "por parte del administrador antes de poder publicar herramientas.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Proveedor registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "422", description = "El correo o el NIT ya estan registrados")
    })
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> registrarProveedor(
            @Valid @RequestBody RegistroProveedorCommand command) {

        Usuario usuario = registrarUsuarioUseCase.registrarProveedor(command);
        UsuarioResponseDto dto = UsuarioDtoMapper.toResponseDto(usuario);

        return ResponseEntity
                .created(URI.create("/api/v1/usuarios/" + usuario.getIdUsuario()))
                .body(ApiResponseDto.ok(
                        "Proveedor registrado con exito. Queda pendiente de verificacion.", dto));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion",
            description = "Valida las credenciales y devuelve el access token y el refresh token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticacion correcta"),
            @ApiResponse(responseCode = "422", description = "Credenciales invalidas o cuenta bloqueada")
    })
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponseDto respuesta = autenticarUsuarioUseCase.login(request);
        return ResponseEntity.ok(ApiResponseDto.ok("Autenticacion exitosa", respuesta));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar el token de acceso",
            description = "Entrega un nuevo access token a partir de un refresh token vigente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado"),
            @ApiResponse(responseCode = "422", description = "Refresh token invalido o expirado")
    })
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> refrescarToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        AuthResponseDto respuesta = autenticarUsuarioUseCase.renovarToken(request.refreshToken());
        return ResponseEntity.ok(ApiResponseDto.ok("Token renovado correctamente", respuesta));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesion",
            description = "Con JWT stateless el servidor no guarda sesion; el cierre consiste en que "
                    + "el frontend borre los tokens de localStorage. El endpoint existe para dejar "
                    + "el flujo documentado y permitir auditoria.")
    @ApiResponse(responseCode = "200", description = "Sesion cerrada en el cliente")
    public ResponseEntity<ApiResponseDto<Void>> logout() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponseDto.ok("Sesion cerrada. Elimine los tokens del almacenamiento local.", null));
    }
}
