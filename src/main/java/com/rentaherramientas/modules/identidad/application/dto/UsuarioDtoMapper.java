package com.rentaherramientas.modules.identidad.application.dto;

import com.rentaherramientas.modules.identidad.domain.model.PerfilCliente;
import com.rentaherramientas.modules.identidad.domain.model.PerfilProveedor;
import com.rentaherramientas.modules.identidad.domain.model.Usuario;

/**
 * Traduce del modelo de dominio a los DTO de salida.
 *
 * Se hace a mano (sin MapStruct) para que el codigo sea facil de leer y de
 * explicar, y para no sumar una dependencia nueva al pom del compañero.
 */
public final class UsuarioDtoMapper {

    private UsuarioDtoMapper() {
        // Clase de utilidades: no se instancia.
    }

    public static UsuarioResponseDto toResponseDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return UsuarioResponseDto.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombreCompleto(usuario.getNombreCompleto())
                .email(usuario.getEmail() != null ? usuario.getEmail().valor() : null)
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .tipoDocumento(usuario.getDocumento() != null ? usuario.getDocumento().tipo().name() : null)
                .numeroDocumento(usuario.getDocumento() != null ? usuario.getDocumento().numero() : null)
                .activo(usuario.isActivo())
                .fechaCreacion(usuario.getFechaCreacion())
                .roles(usuario.nombresDeRoles())
                .perfilProveedor(toPerfilProveedorDto(usuario.getPerfilProveedor()))
                .perfilCliente(toPerfilClienteDto(usuario.getPerfilCliente()))
                .build();
    }

    public static PerfilProveedorDto toPerfilProveedorDto(PerfilProveedor perfil) {
        if (perfil == null) {
            return null;
        }
        return PerfilProveedorDto.builder()
                .idPerfilProveedor(perfil.getIdPerfilProveedor())
                .razonSocial(perfil.getRazonSocial())
                .nombreComercial(perfil.getNombreComercial())
                .ciudadOperacion(perfil.getCiudadOperacion())
                .sitioWeb(perfil.getSitioWeb())
                .fechaVinculacion(perfil.getFechaVinculacion())
                .verificado(perfil.isVerificado())
                .calificacionPromedio(perfil.getCalificacionPromedio())
                .build();
    }

    public static PerfilClienteDto toPerfilClienteDto(PerfilCliente perfil) {
        if (perfil == null) {
            return null;
        }
        return PerfilClienteDto.builder()
                .idPerfilCliente(perfil.getIdPerfilCliente())
                .tipoCliente(perfil.getTipoCliente())
                .ciudadResidencia(perfil.getCiudadResidencia())
                .cupoCredito(perfil.getCupoCredito())
                .fechaRegistro(perfil.getFechaRegistro())
                .alquileresRealizados(perfil.getAlquileresRealizados())
                .build();
    }
}
