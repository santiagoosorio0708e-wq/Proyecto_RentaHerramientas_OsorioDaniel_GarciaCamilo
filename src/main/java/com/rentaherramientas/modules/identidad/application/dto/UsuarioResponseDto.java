package com.rentaherramientas.modules.identidad.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Vista publica de un usuario.
 * Nunca expone el hash de la contrasena: esa es la razon principal de usar DTO
 * en lugar de devolver la entidad directamente.
 */
@Builder
@Schema(description = "Informacion publica de un usuario del sistema")
public record UsuarioResponseDto(

        @Schema(description = "Identificador unico", example = "9")
        Long idUsuario,

        @Schema(description = "Nombre completo", example = "Juan Camilo Garcia")
        String nombreCompleto,

        @Schema(description = "Correo electronico", example = "camilo.garcia@gmail.com")
        String email,

        @Schema(description = "Telefono de contacto", example = "3185550109")
        String telefono,

        @Schema(description = "Direccion registrada", example = "Carrera 33 # 48-19, Bucaramanga")
        String direccion,

        @Schema(description = "Tipo de documento", example = "CC")
        String tipoDocumento,

        @Schema(description = "Numero de documento", example = "1098765433")
        String numeroDocumento,

        @Schema(description = "Indica si la cuenta esta habilitada", example = "true")
        boolean activo,

        @Schema(description = "Fecha de creacion de la cuenta")
        LocalDateTime fechaCreacion,

        @Schema(description = "Roles asignados", example = "[\"ROLE_CLIENTE\"]")
        Set<String> roles,

        @Schema(description = "Perfil de proveedor, solo si el usuario es proveedor")
        PerfilProveedorDto perfilProveedor,

        @Schema(description = "Perfil de cliente, solo si el usuario es cliente")
        PerfilClienteDto perfilCliente
) {
}
