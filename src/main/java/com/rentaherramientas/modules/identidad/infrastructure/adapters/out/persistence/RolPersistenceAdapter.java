package com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence;

import com.rentaherramientas.modules.identidad.domain.model.Rol;
import com.rentaherramientas.modules.identidad.domain.ports.out.RolRepositoryPort;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.mapper.UsuarioPersistenceMapper;
import com.rentaherramientas.modules.identidad.infrastructure.adapters.out.persistence.repository.SpringDataRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Adaptador de salida para la tabla roles. */
@Component
@RequiredArgsConstructor
public class RolPersistenceAdapter implements RolRepositoryPort {

    private final SpringDataRolRepository rolRepository;
    private final UsuarioPersistenceMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> buscarPorNombre(String nombre) {
        return rolRepository.findByNombreIgnoreCase(nombre).map(mapper::toRolDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> buscarPorId(Long idRol) {
        return rolRepository.findById(idRol).map(mapper::toRolDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rol> listarTodos() {
        return rolRepository.findAll().stream().map(mapper::toRolDomain).toList();
    }

    @Override
    @Transactional
    public Rol guardar(Rol rol) {
        return mapper.toRolDomain(rolRepository.save(mapper.toRolEntity(rol)));
    }
}
