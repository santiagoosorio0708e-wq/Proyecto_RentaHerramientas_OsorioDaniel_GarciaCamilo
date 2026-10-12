package com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence;

import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.identidad.domain.model.Rol;
import com.rentaherramientas.modules.identidad.domain.model.Usuario;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Documento;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Email;
import com.rentaherramientas.modules.identidad.domain.ports.out.UsuarioRepositoryPort;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.RolJpaEntity;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.entity.UsuarioJpaEntity;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.mapper.UsuarioPersistenceMapper;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.repository.SpringDataRolRepository;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.repository.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Adaptador de salida: implementa el puerto del dominio usando Spring Data JPA.
 *
 * Es la pieza que "enchufa" el hexagono con MySQL. Si manana se cambiara de
 * motor o de tecnologia de persistencia, solo habria que escribir otro
 * adaptador como este.
 */
@Component
@RequiredArgsConstructor
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final SpringDataUsuarioRepository usuarioRepository;
    private final SpringDataRolRepository rolRepository;
    private final UsuarioPersistenceMapper mapper;

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {

        // En una actualizacion se recupera la fila existente para que Hibernate
        // haga UPDATE sobre la entidad administrada en vez de intentar un INSERT.
        UsuarioJpaEntity existente = (usuario.getIdUsuario() != null)
                ? usuarioRepository.findById(usuario.getIdUsuario()).orElse(null)
                : null;

        Set<RolJpaEntity> rolesJpa = resolverRoles(usuario.getRoles());

        UsuarioJpaEntity aGuardar = mapper.toEntity(usuario, existente, rolesJpa);
        UsuarioJpaEntity guardado = usuarioRepository.save(aGuardar);

        return mapper.toDomain(guardado);
    }

    /**
     * Los roles no se crean desde aqui: deben existir previamente en la tabla
     * roles. Se buscan por nombre para enlazar la fila real y no duplicarla.
     */
    private Set<RolJpaEntity> resolverRoles(Set<Rol> roles) {
        Set<RolJpaEntity> resultado = new HashSet<>();
        if (roles == null) {
            return resultado;
        }
        for (Rol rol : roles) {
            RolJpaEntity entidadRol = rolRepository.findByNombreIgnoreCase(rol.getNombre())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "El rol " + rol.getNombre() + " no existe en la base de datos"));
            resultado.add(entidadRol);
        }
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(Email email) {
        return usuarioRepository.findByEmailIgnoreCase(email.valor()).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorDocumento(Documento documento) {
        return usuarioRepository
                .findByTipoDocumentoAndNumeroDocumento(documento.tipo(), documento.numero())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorEmail(Email email) {
        return usuarioRepository.existsByEmailIgnoreCase(email.valor());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorDocumento(Documento documento) {
        return usuarioRepository.existsByTipoDocumentoAndNumeroDocumento(
                documento.tipo(), documento.numero());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarPorRol(String nombreRol) {
        return usuarioRepository.buscarPorNombreRol(nombreRol).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void eliminarPorId(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("No se encontro el usuario con id " + idUsuario);
        }
        usuarioRepository.deleteById(idUsuario);
    }
}
