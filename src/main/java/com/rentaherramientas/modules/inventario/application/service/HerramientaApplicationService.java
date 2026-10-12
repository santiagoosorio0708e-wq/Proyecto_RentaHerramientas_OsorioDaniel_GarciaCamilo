package com.rentaherramientas.modules.inventario.application.service;

import com.rentaherramientas.common.exception.BusinessRuleException;
import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.inventario.application.dto.ActualizarHerramientaRequest;
import com.rentaherramientas.modules.inventario.application.dto.CrearHerramientaCommand;
import com.rentaherramientas.modules.inventario.application.dto.EspecificacionTecnicaRequest;
import com.rentaherramientas.modules.inventario.application.dto.FiltroCatalogoQuery;
import com.rentaherramientas.modules.inventario.application.dto.ImagenHerramientaRequest;
import com.rentaherramientas.modules.inventario.application.dto.TarifaAlquilerRequest;
import com.rentaherramientas.modules.inventario.domain.model.Categoria;
import com.rentaherramientas.modules.inventario.domain.model.EspecificacionTecnica;
import com.rentaherramientas.modules.inventario.domain.model.EstadoHerramienta;
import com.rentaherramientas.modules.inventario.domain.model.Herramienta;
import com.rentaherramientas.modules.inventario.domain.model.ImagenHerramienta;
import com.rentaherramientas.modules.inventario.domain.model.TarifaAlquiler;
import com.rentaherramientas.modules.inventario.domain.model.TipoTarifa;
import com.rentaherramientas.modules.inventario.domain.ports.in.CambiarEstadoInventarioUseCase;
import com.rentaherramientas.modules.inventario.domain.ports.in.ConsultarCatalogoUseCase;
import com.rentaherramientas.modules.inventario.domain.ports.in.GestionarHerramientaUseCase;
import com.rentaherramientas.modules.inventario.domain.ports.out.CategoriaRepositoryPort;
import com.rentaherramientas.modules.inventario.domain.ports.out.HerramientaRepositoryPort;
import com.rentaherramientas.modules.inventario.domain.ports.out.ProveedorPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de aplicacion del bounded context herramientas-inventario.
 *
 * Implementa los tres puertos de entrada del modulo. Su responsabilidad es
 * coordinar: validar quien pide la operacion, cargar lo necesario desde los
 * repositorios y delegar las reglas al agregado Herramienta.
 */
@Service
@RequiredArgsConstructor
public class HerramientaApplicationService
        implements GestionarHerramientaUseCase, ConsultarCatalogoUseCase, CambiarEstadoInventarioUseCase {

    private static final Logger log = LoggerFactory.getLogger(HerramientaApplicationService.class);

    private final HerramientaRepositoryPort herramientaRepository;
    private final CategoriaRepositoryPort categoriaRepository;
    private final ProveedorPort proveedorPort;

    // ==================================================================
    // CRUD DE HERRAMIENTAS
    // ==================================================================

    @Override
    @Transactional
    public Herramienta crear(String emailSolicitante, CrearHerramientaCommand command) {

        Long idProveedor = proveedorPort.obtenerIdUsuarioPorEmail(emailSolicitante);
        boolean esAdmin = proveedorPort.esAdministrador(emailSolicitante);

        // Un proveedor debe estar verificado por el administrador antes de publicar.
        if (!esAdmin && !proveedorPort.estaVerificado(emailSolicitante)) {
            throw new BusinessRuleException(
                    "Su cuenta de proveedor aun no ha sido verificada por el administrador, "
                            + "por lo que no puede publicar herramientas en el catalogo");
        }

        if (herramientaRepository.existePorNumeroSerie(command.numeroSerie())) {
            throw new BusinessRuleException(
                    "Ya existe una herramienta registrada con el numero de serie " + command.numeroSerie());
        }

        Categoria categoriaPrincipal = buscarCategoria(command.idCategoriaPrincipal());

        Herramienta herramienta = Herramienta.registrar(
                idProveedor,
                categoriaPrincipal,
                command.nombre(),
                command.descripcion(),
                command.marca(),
                command.modelo(),
                command.numeroSerie(),
                command.tarifaDiaria(),
                command.valorReposicion());

        herramienta.actualizarDatos(null, null, null, null, null, command.ubicacionBodega());

        agregarCategoriasAdicionales(herramienta, command.categoriasAdicionales());
        agregarImagenesIniciales(herramienta, command.imagenes());
        agregarEspecificacionesIniciales(herramienta, command.especificaciones());
        agregarTarifasIniciales(herramienta, command.tarifasAdicionales());

        Herramienta guardada = herramientaRepository.guardar(herramienta);
        log.info("Herramienta {} registrada por el proveedor {}", guardada.getIdHerramienta(), idProveedor);
        return guardada;
    }

    private void agregarCategoriasAdicionales(Herramienta herramienta, List<Long> idsCategorias) {
        if (idsCategorias == null) {
            return;
        }
        idsCategorias.stream()
                .filter(java.util.Objects::nonNull)
                .map(this::buscarCategoria)
                .forEach(herramienta::agregarCategoria);
    }

    private void agregarImagenesIniciales(Herramienta herramienta, List<ImagenHerramientaRequest> imagenes) {
        if (imagenes == null) {
            return;
        }
        for (ImagenHerramientaRequest imagen : imagenes) {
            herramienta.agregarImagen(ImagenHerramienta.de(
                    imagen.urlFoto(),
                    imagen.descripcion(),
                    Boolean.TRUE.equals(imagen.principal()),
                    imagen.orden()));
        }
    }

    private void agregarEspecificacionesIniciales(Herramienta herramienta,
                                                  List<EspecificacionTecnicaRequest> especificaciones) {
        if (especificaciones == null) {
            return;
        }
        for (EspecificacionTecnicaRequest especificacion : especificaciones) {
            herramienta.agregarEspecificacion(EspecificacionTecnica.de(
                    especificacion.atributo(),
                    especificacion.valor(),
                    especificacion.unidadMedida()));
        }
    }

    private void agregarTarifasIniciales(Herramienta herramienta, List<TarifaAlquilerRequest> tarifas) {
        if (tarifas == null) {
            return;
        }
        for (TarifaAlquilerRequest tarifa : tarifas) {
            TipoTarifa tipo = TipoTarifa.valueOf(tarifa.tipoTarifa().toUpperCase());
            // La tarifa DIARIA ya se creo en la fabrica del agregado.
            if (tipo == TipoTarifa.DIARIA) {
                continue;
            }
            herramienta.agregarTarifa(TarifaAlquiler.de(tipo, tarifa.valor()));
        }
    }

    @Override
    @Transactional
    public Herramienta actualizar(String emailSolicitante, Long idHerramienta,
                                  ActualizarHerramientaRequest request) {

        Herramienta herramienta = cargarConPermiso(emailSolicitante, idHerramienta);

        herramienta.actualizarDatos(
                request.nombre(),
                request.descripcion(),
                request.marca(),
                request.modelo(),
                request.valorReposicion(),
                request.ubicacionBodega());

        if (request.tarifaDiaria() != null) {
            herramienta.cambiarTarifaDiaria(request.tarifaDiaria());
        }
        if (request.idCategoriaPrincipal() != null) {
            herramienta.cambiarCategoriaPrincipal(buscarCategoria(request.idCategoriaPrincipal()));
        }

        return herramientaRepository.guardar(herramienta);
    }

    @Override
    @Transactional
    public void eliminar(String emailSolicitante, Long idHerramienta) {
        Herramienta herramienta = cargarConPermiso(emailSolicitante, idHerramienta);

        // No se borra una herramienta que esta en poder de un cliente: eso
        // dejaria huerfana la reserva del modulo del companero.
        if (herramienta.getEstadoOperativo() == EstadoHerramienta.ALQUILADA) {
            throw new BusinessRuleException(
                    "No se puede eliminar una herramienta que se encuentra ALQUILADA. "
                            + "Espere su devolucion o cambie su estado.");
        }

        herramientaRepository.eliminarPorId(idHerramienta);
        log.info("Herramienta {} eliminada por {}", idHerramienta, emailSolicitante);
    }

    @Override
    @Transactional
    public Herramienta agregarImagen(String emailSolicitante, Long idHerramienta,
                                     ImagenHerramientaRequest request) {
        Herramienta herramienta = cargarConPermiso(emailSolicitante, idHerramienta);
        herramienta.agregarImagen(ImagenHerramienta.de(
                request.urlFoto(),
                request.descripcion(),
                Boolean.TRUE.equals(request.principal()),
                request.orden()));
        return herramientaRepository.guardar(herramienta);
    }

    @Override
    @Transactional
    public Herramienta agregarEspecificacion(String emailSolicitante, Long idHerramienta,
                                             EspecificacionTecnicaRequest request) {
        Herramienta herramienta = cargarConPermiso(emailSolicitante, idHerramienta);
        herramienta.agregarEspecificacion(EspecificacionTecnica.de(
                request.atributo(), request.valor(), request.unidadMedida()));
        return herramientaRepository.guardar(herramienta);
    }

    @Override
    @Transactional
    public Herramienta agregarTarifa(String emailSolicitante, Long idHerramienta,
                                     TarifaAlquilerRequest request) {
        Herramienta herramienta = cargarConPermiso(emailSolicitante, idHerramienta);
        TipoTarifa tipo = TipoTarifa.valueOf(request.tipoTarifa().toUpperCase());
        herramienta.agregarTarifa(TarifaAlquiler.de(tipo, request.valor()));
        return herramientaRepository.guardar(herramienta);
    }

    @Override
    @Transactional
    public Herramienta agregarCategoria(String emailSolicitante, Long idHerramienta, Long idCategoria) {
        Herramienta herramienta = cargarConPermiso(emailSolicitante, idHerramienta);
        herramienta.agregarCategoria(buscarCategoria(idCategoria));
        return herramientaRepository.guardar(herramienta);
    }

    // ==================================================================
    // CONSULTAS DEL CATALOGO
    // ==================================================================

    @Override
    @Transactional(readOnly = true)
    public Herramienta obtenerPorId(Long idHerramienta) {
        return herramientaRepository.buscarPorId(idHerramienta)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro la herramienta con id " + idHerramienta));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Herramienta> buscar(FiltroCatalogoQuery filtro) {
        return herramientaRepository.buscarPorFiltros(filtro == null ? FiltroCatalogoQuery.vacio() : filtro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Herramienta> listarTodas() {
        return herramientaRepository.listarTodas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Herramienta> listarPorCategoria(Long idCategoria) {
        buscarCategoria(idCategoria);
        return herramientaRepository.listarPorCategoria(idCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Herramienta> listarMiInventario(String emailProveedor) {
        Long idProveedor = proveedorPort.obtenerIdUsuarioPorEmail(emailProveedor);
        return herramientaRepository.listarPorProveedor(idProveedor);
    }

    // ==================================================================
    // CONTROL DE ESTADOS
    // ==================================================================

    @Override
    @Transactional
    public Herramienta cambiarEstado(String emailSolicitante, Long idHerramienta,
                                     EstadoHerramienta nuevoEstado, String motivo) {

        Herramienta herramienta = cargarConPermiso(emailSolicitante, idHerramienta);
        EstadoHerramienta anterior = herramienta.getEstadoOperativo();

        herramienta.cambiarEstado(nuevoEstado);

        log.info("Herramienta {}: estado {} -> {} por {}. Motivo: {}",
                idHerramienta, anterior, nuevoEstado, emailSolicitante,
                motivo == null ? "no indicado" : motivo);

        return herramientaRepository.guardar(herramienta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Herramienta> listarPorEstado(EstadoHerramienta estado) {
        return herramientaRepository.listarPorEstado(estado);
    }

    // ==================================================================
    // Metodos de apoyo
    // ==================================================================

    private Categoria buscarCategoria(Long idCategoria) {
        if (idCategoria == null) {
            throw new BusinessRuleException("El identificador de la categoria es obligatorio");
        }
        return categoriaRepository.buscarPorId(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro la categoria con id " + idCategoria));
    }

    /**
     * Carga la herramienta y verifica que quien la pide tenga derecho a tocarla.
     *
     * Esta validacion es la que impide que el proveedor A edite o borre las
     * herramientas del proveedor B. El administrador si puede intervenir
     * cualquier herramienta.
     */
    private Herramienta cargarConPermiso(String emailSolicitante, Long idHerramienta) {
        Herramienta herramienta = obtenerPorId(idHerramienta);

        if (proveedorPort.esAdministrador(emailSolicitante)) {
            return herramienta;
        }

        Long idSolicitante = proveedorPort.obtenerIdUsuarioPorEmail(emailSolicitante);
        if (!herramienta.perteneceA(idSolicitante)) {
            throw new BusinessRuleException(
                    "La herramienta " + idHerramienta + " pertenece a otro proveedor");
        }
        return herramienta;
    }
}
