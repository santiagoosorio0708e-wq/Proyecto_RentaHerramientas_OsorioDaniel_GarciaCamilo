package com.rentaherramientas.modules.inventario.application.service;

import com.rentaherramientas.common.exception.BusinessRuleException;
import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.inventario.application.dto.CrearHerramientaCommand;
import com.rentaherramientas.modules.inventario.domain.model.Categoria;
import com.rentaherramientas.modules.inventario.domain.model.EstadoHerramienta;
import com.rentaherramientas.modules.inventario.domain.model.Herramienta;
import com.rentaherramientas.modules.inventario.domain.model.TipoTarifa;
import com.rentaherramientas.modules.inventario.domain.ports.out.CategoriaRepositoryPort;
import com.rentaherramientas.modules.inventario.domain.ports.out.HerramientaRepositoryPort;
import com.rentaherramientas.modules.inventario.domain.ports.out.ProveedorPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias del servicio de inventario y de las reglas del agregado Herramienta. */
@ExtendWith(MockitoExtension.class)
@DisplayName("HerramientaApplicationService - inventario y control de estados")
class HerramientaApplicationServiceTest {

    @Mock
    private HerramientaRepositoryPort herramientaRepository;

    @Mock
    private CategoriaRepositoryPort categoriaRepository;

    @Mock
    private ProveedorPort proveedorPort;

    @InjectMocks
    private HerramientaApplicationService herramientaService;

    private static final String EMAIL_PROVEEDOR = "ventas@maquiantioquia.com";
    private static final Long ID_PROVEEDOR = 4L;

    private Categoria categoria;
    private CrearHerramientaCommand comando;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .idCategoria(2L)
                .nombre("Demolicion y Perforacion")
                .descripcion("Martillos y rotomartillos")
                .build();

        comando = new CrearHerramientaCommand(
                2L,
                "Martillo demoledor electrico",
                "Rompedor de concreto reforzado de 1500W",
                "Bosch",
                "GSH 11E",
                "SN-BSH-9001",
                new BigDecimal("120000"),
                new BigDecimal("3500000"),
                "Pasillo B - Estante 4",
                null, null, null, null);
    }

    // ------------------------------------------------------------------
    // Alta de herramientas
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Registra la herramienta y le crea automaticamente la tarifa DIARIA")
    void creaHerramientaConTarifaDiaria() {
        when(proveedorPort.obtenerIdUsuarioPorEmail(EMAIL_PROVEEDOR)).thenReturn(ID_PROVEEDOR);
        when(proveedorPort.esAdministrador(EMAIL_PROVEEDOR)).thenReturn(false);
        when(proveedorPort.estaVerificado(EMAIL_PROVEEDOR)).thenReturn(true);
        when(herramientaRepository.existePorNumeroSerie(anyString())).thenReturn(false);
        when(categoriaRepository.buscarPorId(2L)).thenReturn(Optional.of(categoria));
        when(herramientaRepository.guardar(any(Herramienta.class))).thenAnswer(inv -> inv.getArgument(0));

        Herramienta resultado = herramientaService.crear(EMAIL_PROVEEDOR, comando);

        assertNotNull(resultado);
        assertEquals(EstadoHerramienta.DISPONIBLE, resultado.getEstadoOperativo());
        assertEquals(ID_PROVEEDOR, resultado.getIdProveedor());
        assertTrue(resultado.buscarTarifa(TipoTarifa.DIARIA).isPresent(),
                "Al registrar debe quedar creada la tarifa diaria");
        assertEquals(new BigDecimal("120000"),
                resultado.buscarTarifa(TipoTarifa.DIARIA).orElseThrow().getValor());
    }

    @Test
    @DisplayName("Un proveedor sin verificar no puede publicar herramientas")
    void proveedorSinVerificarNoPublica() {
        when(proveedorPort.obtenerIdUsuarioPorEmail(EMAIL_PROVEEDOR)).thenReturn(ID_PROVEEDOR);
        when(proveedorPort.esAdministrador(EMAIL_PROVEEDOR)).thenReturn(false);
        when(proveedorPort.estaVerificado(EMAIL_PROVEEDOR)).thenReturn(false);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> herramientaService.crear(EMAIL_PROVEEDOR, comando));

        assertTrue(ex.getMessage().toLowerCase().contains("verificada"));
        verify(herramientaRepository, never()).guardar(any(Herramienta.class));
    }

    @Test
    @DisplayName("Rechaza un numero de serie que ya existe")
    void rechazaNumeroSerieDuplicado() {
        when(proveedorPort.obtenerIdUsuarioPorEmail(EMAIL_PROVEEDOR)).thenReturn(ID_PROVEEDOR);
        when(proveedorPort.esAdministrador(EMAIL_PROVEEDOR)).thenReturn(false);
        when(proveedorPort.estaVerificado(EMAIL_PROVEEDOR)).thenReturn(true);
        when(herramientaRepository.existePorNumeroSerie("SN-BSH-9001")).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> herramientaService.crear(EMAIL_PROVEEDOR, comando));

        assertTrue(ex.getMessage().contains("numero de serie"));
    }

    // ------------------------------------------------------------------
    // Propiedad de la herramienta
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Un proveedor no puede modificar herramientas de otro proveedor")
    void proveedorNoTocaInventarioAjeno() {
        Herramienta ajena = herramientaDePrueba(99L, EstadoHerramienta.DISPONIBLE);

        when(herramientaRepository.buscarPorId(1L)).thenReturn(Optional.of(ajena));
        when(proveedorPort.esAdministrador(EMAIL_PROVEEDOR)).thenReturn(false);
        when(proveedorPort.obtenerIdUsuarioPorEmail(EMAIL_PROVEEDOR)).thenReturn(ID_PROVEEDOR);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> herramientaService.eliminar(EMAIL_PROVEEDOR, 1L));

        assertTrue(ex.getMessage().contains("otro proveedor"));
        verify(herramientaRepository, never()).eliminarPorId(1L);
    }

    @Test
    @DisplayName("El administrador si puede intervenir cualquier herramienta")
    void administradorPuedeTodo() {
        Herramienta ajena = herramientaDePrueba(99L, EstadoHerramienta.DISPONIBLE);

        when(herramientaRepository.buscarPorId(1L)).thenReturn(Optional.of(ajena));
        when(proveedorPort.esAdministrador("admin.perez@rentatool.com")).thenReturn(true);

        herramientaService.eliminar("admin.perez@rentatool.com", 1L);

        verify(herramientaRepository).eliminarPorId(1L);
    }

    @Test
    @DisplayName("No se puede eliminar una herramienta ALQUILADA")
    void noEliminaHerramientaAlquilada() {
        Herramienta alquilada = herramientaDePrueba(ID_PROVEEDOR, EstadoHerramienta.ALQUILADA);

        when(herramientaRepository.buscarPorId(1L)).thenReturn(Optional.of(alquilada));
        when(proveedorPort.esAdministrador(EMAIL_PROVEEDOR)).thenReturn(false);
        when(proveedorPort.obtenerIdUsuarioPorEmail(EMAIL_PROVEEDOR)).thenReturn(ID_PROVEEDOR);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> herramientaService.eliminar(EMAIL_PROVEEDOR, 1L));

        assertTrue(ex.getMessage().contains("ALQUILADA"));
        verify(herramientaRepository, never()).eliminarPorId(1L);
    }

    @Test
    @DisplayName("Lanza 404 cuando la herramienta no existe")
    void herramientaInexistente() {
        when(herramientaRepository.buscarPorId(777L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> herramientaService.obtenerPorId(777L));
    }

    // ------------------------------------------------------------------
    // Reglas de estado del agregado
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Permite pasar de DISPONIBLE a EN_MANTENIMIENTO")
    void transicionValida() {
        Herramienta herramienta = herramientaDePrueba(ID_PROVEEDOR, EstadoHerramienta.DISPONIBLE);

        when(herramientaRepository.buscarPorId(1L)).thenReturn(Optional.of(herramienta));
        when(proveedorPort.esAdministrador(EMAIL_PROVEEDOR)).thenReturn(false);
        when(proveedorPort.obtenerIdUsuarioPorEmail(EMAIL_PROVEEDOR)).thenReturn(ID_PROVEEDOR);
        when(herramientaRepository.guardar(any(Herramienta.class))).thenAnswer(inv -> inv.getArgument(0));

        Herramienta resultado = herramientaService.cambiarEstado(
                EMAIL_PROVEEDOR, 1L, EstadoHerramienta.EN_MANTENIMIENTO, "Revision de 500 horas");

        assertEquals(EstadoHerramienta.EN_MANTENIMIENTO, resultado.getEstadoOperativo());
    }

    @Test
    @DisplayName("Impide pasar de FUERA_DE_SERVICIO directamente a DISPONIBLE")
    void transicionInvalida() {
        Herramienta dadaDeBaja = herramientaDePrueba(ID_PROVEEDOR, EstadoHerramienta.FUERA_DE_SERVICIO);

        when(herramientaRepository.buscarPorId(1L)).thenReturn(Optional.of(dadaDeBaja));
        when(proveedorPort.esAdministrador(EMAIL_PROVEEDOR)).thenReturn(false);
        when(proveedorPort.obtenerIdUsuarioPorEmail(EMAIL_PROVEEDOR)).thenReturn(ID_PROVEEDOR);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> herramientaService.cambiarEstado(
                        EMAIL_PROVEEDOR, 1L, EstadoHerramienta.DISPONIBLE, null));

        assertTrue(ex.getMessage().contains("No se permite pasar"));
    }

    @Test
    @DisplayName("Solo una herramienta DISPONIBLE se considera alquilable")
    void disponibilidadSegunEstado() {
        assertTrue(herramientaDePrueba(ID_PROVEEDOR, EstadoHerramienta.DISPONIBLE).estaDisponible());
        assertEquals(false, herramientaDePrueba(ID_PROVEEDOR, EstadoHerramienta.ALQUILADA).estaDisponible());
        assertEquals(false, herramientaDePrueba(ID_PROVEEDOR, EstadoHerramienta.EN_MANTENIMIENTO).estaDisponible());
    }

    // ------------------------------------------------------------------
    // Cotizacion
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Cotizar 3 dias usa la tarifa diaria")
    void cotizaConTarifaDiaria() {
        Herramienta herramienta = herramientaDePrueba(ID_PROVEEDOR, EstadoHerramienta.DISPONIBLE);

        BigDecimal total = herramienta.cotizarPorDias(3);

        assertEquals(0, new BigDecimal("360000").compareTo(total),
                "3 dias x 120000 = 360000");
    }

    @Test
    @DisplayName("Cotizar 0 dias o menos es un error de negocio")
    void cotizaConDiasInvalidos() {
        Herramienta herramienta = herramientaDePrueba(ID_PROVEEDOR, EstadoHerramienta.DISPONIBLE);

        assertThrows(BusinessRuleException.class, () -> herramienta.cotizarPorDias(0));
        assertThrows(BusinessRuleException.class, () -> herramienta.cotizarPorDias(-5));
    }

    @Test
    @DisplayName("No permite dos tarifas del mismo tipo en la misma herramienta")
    void noPermiteTarifaRepetida() {
        Herramienta herramienta = herramientaDePrueba(ID_PROVEEDOR, EstadoHerramienta.DISPONIBLE);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> herramienta.agregarTarifa(
                        com.rentaherramientas.modules.inventario.domain.model.TarifaAlquiler.de(
                                TipoTarifa.DIARIA, new BigDecimal("99000"))));

        assertTrue(ex.getMessage().contains("DIARIA"));
    }

    @Test
    @DisplayName("La primera imagen de la galeria queda como portada automaticamente")
    void primeraImagenEsPortada() {
        Herramienta herramienta = herramientaDePrueba(ID_PROVEEDOR, EstadoHerramienta.DISPONIBLE);

        herramienta.agregarImagen(
                com.rentaherramientas.modules.inventario.domain.model.ImagenHerramienta.de(
                        "https://cdn.rentatool.com/a.jpg", "Vista frontal", false, 1));

        assertTrue(herramienta.imagenPrincipal().isPresent());
        assertEquals("https://cdn.rentatool.com/a.jpg",
                herramienta.imagenPrincipal().orElseThrow().getUrlFoto());
    }

    @Test
    @DisplayName("Al marcar una nueva portada la anterior deja de serlo")
    void soloUnaPortada() {
        Herramienta herramienta = herramientaDePrueba(ID_PROVEEDOR, EstadoHerramienta.DISPONIBLE);

        herramienta.agregarImagen(
                com.rentaherramientas.modules.inventario.domain.model.ImagenHerramienta.de(
                        "https://cdn.rentatool.com/a.jpg", "Primera", true, 1));
        herramienta.agregarImagen(
                com.rentaherramientas.modules.inventario.domain.model.ImagenHerramienta.de(
                        "https://cdn.rentatool.com/b.jpg", "Segunda", true, 2));

        List<?> principales = herramienta.getImagenes().stream()
                .filter(com.rentaherramientas.modules.inventario.domain.model.ImagenHerramienta::isPrincipal)
                .toList();

        assertEquals(1, principales.size(), "Solo puede haber una foto de portada");
        assertEquals("https://cdn.rentatool.com/b.jpg",
                herramienta.imagenPrincipal().orElseThrow().getUrlFoto());
    }

    // ------------------------------------------------------------------
    // Utilidad
    // ------------------------------------------------------------------

    private Herramienta herramientaDePrueba(Long idProveedor, EstadoHerramienta estado) {
        Herramienta herramienta = Herramienta.registrar(
                idProveedor,
                categoria,
                "Martillo demoledor electrico",
                "Rompedor de concreto reforzado",
                "Bosch",
                "GSH 11E",
                "SN-BSH-9001",
                new BigDecimal("120000"),
                new BigDecimal("3500000"));

        // Se fuerza el estado sin pasar por las validaciones de transicion,
        // porque aqui solo interesa preparar el escenario de la prueba.
        return Herramienta.builder()
                .idHerramienta(1L)
                .idProveedor(herramienta.getIdProveedor())
                .nombre(herramienta.getNombre())
                .descripcion(herramienta.getDescripcion())
                .marca(herramienta.getMarca())
                .modelo(herramienta.getModelo())
                .numeroSerie(herramienta.getNumeroSerie())
                .estadoOperativo(estado)
                .tarifaDiaria(herramienta.getTarifaDiaria())
                .valorReposicion(herramienta.getValorReposicion())
                .fechaRegistro(herramienta.getFechaRegistro())
                .categoriaPrincipal(categoria)
                .categorias(herramienta.getCategorias())
                .imagenes(herramienta.getImagenes())
                .especificaciones(herramienta.getEspecificaciones())
                .tarifas(herramienta.getTarifas())
                .build();
    }
}
