package com.rentaherramientas.modules.identidad.infrastructure.adapters.in.web;

import com.rentaherramientas.common.dto.ApiResponseDto;
import com.rentaherramientas.modules.identidad.application.dto.ActualizarPerfilRequest;
import com.rentaherramientas.modules.identidad.application.dto.CambiarPasswordRequest;
import com.rentaherramientas.modules.identidad.application.dto.UsuarioDtoMapper;
import com.rentaherramientas.modules.identidad.application.dto.UsuarioResponseDto;
import com.rentaherramientas.modules.identidad.domain.model.Usuario;
import com.rentaherramientas.modules.identidad.domain.ports.in.GestionarUsuarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador de entrada REST para la gestion de usuarios.
 *
 * La autorizacion se declara con @PreAuthorize sobre cada metodo. Esto permite
 * un control mas fino que el de SecurityFilterChain: aqui se distingue entre lo
 * que puede ver el propio usuario y lo que solo puede ver el administrador.
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "2. Usuarios y Perfiles",
        description = "Consulta de usuarios, perfil propio y administracion de cuentas")
public class UsuarioController {

    private final GestionarUsuarioUseCase gestionarUsuarioUseCase;

    // ------------------------------------------------------------------
    // Endpoints del usuario autenticado
    // ------------------------------------------------------------------

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar mi propio perfil",
            description = "Devuelve los datos del usuario dueno del token enviado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil obtenido"),
            @ApiResponse(responseCode = "401", description = "Token ausente, invalido o expirado")
    })
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> miPerfil(
            @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = gestionarUsuarioUseCase.obtenerPorEmail(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponseDto.ok(UsuarioDtoMapper.toResponseDto(usuario)));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Actualizar mis datos de contacto",
            description = "Permite cambiar nombre, telefono y direccion del usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> actualizarMiPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ActualizarPerfilRequest request) {

        Usuario usuario = gestionarUsuarioUseCase.actualizarPerfil(userDetails.getUsername(), request);
        return ResponseEntity.ok(
                ApiResponseDto.ok("Perfil actualizado correctamente", UsuarioDtoMapper.toResponseDto(usuario)));
    }

    @PatchMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cambiar mi contrasena",
            description = "Valida la contrasena actual antes de reemplazarla por la nueva.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrasena actualizada"),
            @ApiResponse(responseCode = "422", description = "La contrasena actual no coincide")
    })
    public ResponseEntity<ApiResponseDto<Void>> cambiarPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CambiarPasswordRequest request) {

        gestionarUsuarioUseCase.cambiarPassword(
                userDetails.getUsername(), request.passwordActual(), request.passwordNueva());

        return ResponseEntity.ok(ApiResponseDto.ok("Contrasena actualizada correctamente", null));
    }

    // ------------------------------------------------------------------
    // Endpoints del administrador
    // ------------------------------------------------------------------

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios",
            description = "Tabla de control del administrador. Se puede filtrar por rol "
                    + "con el parametro opcional rol (ej. CLIENTE o ROLE_CLIENTE).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "403", description = "Solo el administrador puede listar usuarios")
    })
    public ResponseEntity<ApiResponseDto<List<UsuarioResponseDto>>> listarUsuarios(
            @Parameter(description = "Filtro opcional por rol", example = "CLIENTE")
            @RequestParam(required = false) String rol) {

        List<Usuario> usuarios = (rol == null || rol.isBlank())
                ? gestionarUsuarioUseCase.listarTodos()
                : gestionarUsuarioUseCase.listarPorRol(rol);

        List<UsuarioResponseDto> dtos = usuarios.stream().map(UsuarioDtoMapper::toResponseDto).toList();
        return ResponseEntity.ok(ApiResponseDto.ok("Usuarios obtenidos correctamente", dtos));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Consultar un usuario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "El usuario no existe")
    })
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> obtenerPorId(
            @Parameter(description = "Identificador del usuario", example = "9")
            @PathVariable Long id) {

        Usuario usuario = gestionarUsuarioUseCase.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponseDto.ok(UsuarioDtoMapper.toResponseDto(usuario)));
    }

    @PatchMapping("/{id}/bloquear")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Bloquear la cuenta de un usuario",
            description = "Desactiva la cuenta. El usuario no podra volver a iniciar sesion "
                    + "hasta que se reactive. No se permite bloquear administradores.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta bloqueada"),
            @ApiResponse(responseCode = "422", description = "La cuenta ya estaba bloqueada o es de un administrador")
    })
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> bloquear(@PathVariable Long id) {
        Usuario usuario = gestionarUsuarioUseCase.bloquearUsuario(id);
        return ResponseEntity.ok(
                ApiResponseDto.ok("Cuenta bloqueada correctamente", UsuarioDtoMapper.toResponseDto(usuario)));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reactivar la cuenta de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta activada"),
            @ApiResponse(responseCode = "422", description = "La cuenta ya estaba activa")
    })
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> activar(@PathVariable Long id) {
        Usuario usuario = gestionarUsuarioUseCase.activarUsuario(id);
        return ResponseEntity.ok(
                ApiResponseDto.ok("Cuenta activada correctamente", UsuarioDtoMapper.toResponseDto(usuario)));
    }

    @PatchMapping("/{id}/verificar-proveedor")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Verificar la documentacion de un proveedor",
            description = "Habilita al proveedor para publicar herramientas en el catalogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proveedor verificado"),
            @ApiResponse(responseCode = "422", description = "El usuario no es proveedor o ya estaba verificado")
    })
    public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> verificarProveedor(@PathVariable Long id) {
        Usuario usuario = gestionarUsuarioUseCase.verificarProveedor(id);
        return ResponseEntity.ok(
                ApiResponseDto.ok("Proveedor verificado correctamente", UsuarioDtoMapper.toResponseDto(usuario)));
    }
}
