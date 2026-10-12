package com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.mapper;

import com.rentaherramientas.modules.identidad.domain.model.PerfilCliente;
import com.rentaherramientas.modules.identidad.domain.model.PerfilProveedor;
import com.rentaherramientas.modules.identidad.domain.model.Permiso;
import com.rentaherramientas.modules.identidad.domain.model.Rol;
import com.rentaherramientas.modules.identidad.domain.model.Usuario;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Documento;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Email;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Password;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.PerfilClienteJpaEntity;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.PerfilProveedorJpaEntity;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.PermisoJpaEntity;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.RolJpaEntity;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Traductor entre el modelo de dominio y las entidades JPA.
 *
 * Esta clase es la que hace posible que el dominio este limpio de anotaciones
 * de persistencia. Es el precio de la arquitectura hexagonal: un poco mas de
 * codigo de traduccion a cambio de un dominio independiente de la base de datos.
 */
@Component
public class UsuarioPersistenceMapper {

    // ------------------------------------------------------------------
    // JPA -> DOMINIO
    // ------------------------------------------------------------------
    public Usuario toDomain(UsuarioJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Usuario.builder()
                .idUsuario(entity.getIdUsuario())
                .nombreCompleto(entity.getNombreCompleto())
                .email(new Email(entity.getEmail()))
                .password(Password.deHash(entity.getPasswordHash()))
                .telefono(entity.getTelefono())
                .direccion(entity.getDireccion())
                .documento(new Documento(entity.getTipoDocumento(), entity.getNumeroDocumento()))
                .activo(entity.isActivo())
                .fechaCreacion(entity.getFechaCreacion())
                .roles(toRolesDomain(entity.getRoles()))
                .perfilProveedor(toPerfilProveedorDomain(entity.getPerfilProveedor()))
                .perfilCliente(toPerfilClienteDomain(entity.getPerfilCliente()))
                .build();
    }

    public Set<Rol> toRolesDomain(Set<RolJpaEntity> entidades) {
        if (entidades == null) {
            return new HashSet<>();
        }
        return entidades.stream().map(this::toRolDomain).collect(Collectors.toCollection(HashSet::new));
    }

    public Rol toRolDomain(RolJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Set<Permiso> permisos = entity.getPermisos() == null
                ? new HashSet<>()
                : entity.getPermisos().stream()
                        .map(this::toPermisoDomain)
                        .collect(Collectors.toCollection(HashSet::new));

        return Rol.builder()
                .idRol(entity.getIdRol())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .permisos(permisos)
                .build();
    }

    public Permiso toPermisoDomain(PermisoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Permiso.builder()
                .idPermiso(entity.getIdPermiso())
                .codigo(entity.getCodigo())
                .descripcion(entity.getDescripcion())
                .build();
    }

    public PerfilProveedor toPerfilProveedorDomain(PerfilProveedorJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return PerfilProveedor.builder()
                .idPerfilProveedor(entity.getIdPerfilProveedor())
                .idUsuario(entity.getUsuario() != null ? entity.getUsuario().getIdUsuario() : null)
                .razonSocial(entity.getRazonSocial())
                .nombreComercial(entity.getNombreComercial())
                .ciudadOperacion(entity.getCiudadOperacion())
                .sitioWeb(entity.getSitioWeb())
                .fechaVinculacion(entity.getFechaVinculacion())
                .verificado(entity.isVerificado())
                .calificacionPromedio(entity.getCalificacionPromedio())
                .build();
    }

    public PerfilCliente toPerfilClienteDomain(PerfilClienteJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return PerfilCliente.builder()
                .idPerfilCliente(entity.getIdPerfilCliente())
                .idUsuario(entity.getUsuario() != null ? entity.getUsuario().getIdUsuario() : null)
                .tipoCliente(entity.getTipoCliente())
                .ciudadResidencia(entity.getCiudadResidencia())
                .cupoCredito(entity.getCupoCredito())
                .fechaRegistro(entity.getFechaRegistro())
                .alquileresRealizados(entity.getAlquileresRealizados())
                .build();
    }

    // ------------------------------------------------------------------
    // DOMINIO -> JPA
    // ------------------------------------------------------------------

    /**
     * Vuelca el estado del dominio sobre una entidad JPA.
     *
     * Si se recibe una entidad existente (caso actualizacion) se reutiliza para
     * que Hibernate haga UPDATE y no INSERT, y para no perder la version que ya
     * esta administrada por el contexto de persistencia.
     */
    public UsuarioJpaEntity toEntity(Usuario usuario, UsuarioJpaEntity existente, Set<RolJpaEntity> rolesJpa) {

        UsuarioJpaEntity entity = (existente != null) ? existente : new UsuarioJpaEntity();

        entity.setIdUsuario(usuario.getIdUsuario());
        entity.setNombreCompleto(usuario.getNombreCompleto());
        entity.setEmail(usuario.getEmail().valor());
        entity.setPasswordHash(usuario.getPassword().hash());
        entity.setTelefono(usuario.getTelefono());
        entity.setDireccion(usuario.getDireccion());
        entity.setTipoDocumento(usuario.getDocumento().tipo());
        entity.setNumeroDocumento(usuario.getDocumento().numero());
        entity.setActivo(usuario.isActivo());
        entity.setFechaCreacion(usuario.getFechaCreacion());

        if (rolesJpa != null) {
            entity.setRoles(rolesJpa);
        }

        aplicarPerfilProveedor(usuario, entity);
        aplicarPerfilCliente(usuario, entity);

        return entity;
    }

    private void aplicarPerfilProveedor(Usuario usuario, UsuarioJpaEntity entity) {
        PerfilProveedor perfil = usuario.getPerfilProveedor();
        if (perfil == null) {
            return;
        }
        PerfilProveedorJpaEntity destino = entity.getPerfilProveedor() != null
                ? entity.getPerfilProveedor()
                : new PerfilProveedorJpaEntity();

        destino.setRazonSocial(perfil.getRazonSocial());
        destino.setNombreComercial(perfil.getNombreComercial());
        destino.setCiudadOperacion(perfil.getCiudadOperacion());
        destino.setSitioWeb(perfil.getSitioWeb());
        destino.setFechaVinculacion(perfil.getFechaVinculacion());
        destino.setVerificado(perfil.isVerificado());
        destino.setCalificacionPromedio(perfil.getCalificacionPromedio());

        entity.enlazarPerfilProveedor(destino);
    }

    private void aplicarPerfilCliente(Usuario usuario, UsuarioJpaEntity entity) {
        PerfilCliente perfil = usuario.getPerfilCliente();
        if (perfil == null) {
            return;
        }
        PerfilClienteJpaEntity destino = entity.getPerfilCliente() != null
                ? entity.getPerfilCliente()
                : new PerfilClienteJpaEntity();

        destino.setTipoCliente(perfil.getTipoCliente());
        destino.setCiudadResidencia(perfil.getCiudadResidencia());
        destino.setCupoCredito(perfil.getCupoCredito());
        destino.setFechaRegistro(perfil.getFechaRegistro());
        destino.setAlquileresRealizados(perfil.getAlquileresRealizados());

        entity.enlazarPerfilCliente(destino);
    }

    public RolJpaEntity toRolEntity(Rol rol) {
        if (rol == null) {
            return null;
        }
        return RolJpaEntity.builder()
                .idRol(rol.getIdRol())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .build();
    }
}
