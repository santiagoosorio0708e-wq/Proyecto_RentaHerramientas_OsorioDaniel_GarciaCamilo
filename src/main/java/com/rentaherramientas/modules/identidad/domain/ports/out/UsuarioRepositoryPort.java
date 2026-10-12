package com.rentaherramientas.modules.identidad.domain.ports.out;

import com.rentaherramientas.modules.identidad.domain.model.Usuario;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Documento;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Email;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida hacia la persistencia de usuarios.
 *
 * El dominio define QUE necesita; quien lo implementa (UsuarioPersistenceAdapter
 * con JPA) vive en infraestructura. Gracias a esto se puede cambiar MySQL por
 * otro motor sin tocar una sola linea del dominio ni de los casos de uso.
 */
public interface UsuarioRepositoryPort {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long idUsuario);

    Optional<Usuario> buscarPorEmail(Email email);

    Optional<Usuario> buscarPorDocumento(Documento documento);

    boolean existePorEmail(Email email);

    boolean existePorDocumento(Documento documento);

    List<Usuario> listarTodos();

    List<Usuario> listarPorRol(String nombreRol);

    void eliminarPorId(Long idUsuario);
}
