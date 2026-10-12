package com.rentaherramientas.modules.inventario.domain.model;

import com.rentaherramientas.common.exception.BusinessRuleException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Raiz del agregado de Inventario.
 *
 * Una herramienta agrupa sus categorias, su galeria de fotos, su ficha tecnica
 * y sus tarifas. Todo se modifica a traves de esta clase para que las reglas se
 * cumplan siempre en un solo lugar.
 *
 * Igual que Usuario, es una clase de dominio pura: sin JPA y sin Spring.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Herramienta {

    private Long idHerramienta;
    private Long idProveedor;
    private String nombre;
    private String descripcion;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private EstadoHerramienta estadoOperativo;
    private BigDecimal tarifaDiaria;
    private BigDecimal valorReposicion;
    private String ubicacionBodega;
    private LocalDateTime fechaRegistro;

    /** Categoria principal, la que ya existe como columna en la tabla herramientas. */
    private Categoria categoriaPrincipal;

    /** Categorias adicionales (4FN: tabla herramientas_categorias). */
    @Builder.Default
    private Set<Categoria> categorias = new HashSet<>();

    @Builder.Default
    private List<ImagenHerramienta> imagenes = new ArrayList<>();

    @Builder.Default
    private List<EspecificacionTecnica> especificaciones = new ArrayList<>();

    @Builder.Default
    private List<TarifaAlquiler> tarifas = new ArrayList<>();

    // ------------------------------------------------------------------
    // Fabrica
    // ------------------------------------------------------------------
    public static Herramienta registrar(Long idProveedor,
                                        Categoria categoriaPrincipal,
                                        String nombre,
                                        String descripcion,
                                        String marca,
                                        String modelo,
                                        String numeroSerie,
                                        BigDecimal tarifaDiaria,
                                        BigDecimal valorReposicion) {

        if (idProveedor == null) {
            throw new BusinessRuleException("Toda herramienta debe pertenecer a un proveedor");
        }
        if (categoriaPrincipal == null) {
            throw new BusinessRuleException("La categoria principal es obligatoria");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessRuleException("El nombre de la herramienta es obligatorio");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new BusinessRuleException("La descripcion de la herramienta es obligatoria");
        }
        if (marca == null || marca.isBlank()) {
            throw new BusinessRuleException("La marca es obligatoria");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new BusinessRuleException("El modelo es obligatorio");
        }
        if (numeroSerie == null || numeroSerie.isBlank()) {
            throw new BusinessRuleException("El numero de serie es obligatorio");
        }
        validarTarifa(tarifaDiaria);
        if (valorReposicion == null || valorReposicion.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El valor de reposicion debe ser mayor que cero");
        }

        Herramienta herramienta = Herramienta.builder()
                .idProveedor(idProveedor)
                .categoriaPrincipal(categoriaPrincipal)
                .nombre(nombre.trim())
                .descripcion(descripcion.trim())
                .marca(marca.trim())
                .modelo(modelo.trim())
                .numeroSerie(numeroSerie.trim().toUpperCase())
                .estadoOperativo(EstadoHerramienta.DISPONIBLE)
                .tarifaDiaria(tarifaDiaria)
                .valorReposicion(valorReposicion)
                .fechaRegistro(LocalDateTime.now())
                .categorias(new HashSet<>())
                .imagenes(new ArrayList<>())
                .especificaciones(new ArrayList<>())
                .tarifas(new ArrayList<>())
                .build();

        // La tarifa diaria de la columna y la de la tabla de tarifas deben nacer iguales.
        herramienta.agregarTarifa(TarifaAlquiler.de(TipoTarifa.DIARIA, tarifaDiaria));
        herramienta.agregarCategoria(categoriaPrincipal);

        return herramienta;
    }

    private static void validarTarifa(BigDecimal tarifa) {
        if (tarifa == null || tarifa.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("La tarifa diaria debe ser mayor que cero");
        }
    }

    // ------------------------------------------------------------------
    // Reglas de disponibilidad y estado
    // ------------------------------------------------------------------

    public boolean estaDisponible() {
        return estadoOperativo != null && estadoOperativo.permiteAlquiler();
    }

    /**
     * Cambia el estado validando que la transicion tenga sentido.
     * Por ejemplo, no se puede pasar de FUERA_DE_SERVICIO directo a DISPONIBLE.
     */
    public void cambiarEstado(EstadoHerramienta nuevoEstado) {
        if (nuevoEstado == null) {
            throw new BusinessRuleException("El nuevo estado es obligatorio");
        }
        if (this.estadoOperativo == nuevoEstado) {
            throw new BusinessRuleException(
                    "La herramienta ya se encuentra en estado " + nuevoEstado.name());
        }
        if (!this.estadoOperativo.puedeTransicionarA(nuevoEstado)) {
            throw new BusinessRuleException(
                    "No se permite pasar de " + this.estadoOperativo.name()
                            + " a " + nuevoEstado.name());
        }
        this.estadoOperativo = nuevoEstado;
    }

    public void enviarAMantenimiento() {
        cambiarEstado(EstadoHerramienta.EN_MANTENIMIENTO);
    }

    public void darDeBaja() {
        cambiarEstado(EstadoHerramienta.FUERA_DE_SERVICIO);
    }

    // ------------------------------------------------------------------
    // Edicion de datos
    // ------------------------------------------------------------------

    public void actualizarDatos(String nombre, String descripcion, String marca,
                                String modelo, BigDecimal valorReposicion, String ubicacionBodega) {
        if (nombre != null && !nombre.isBlank()) {
            this.nombre = nombre.trim();
        }
        if (descripcion != null && !descripcion.isBlank()) {
            this.descripcion = descripcion.trim();
        }
        if (marca != null && !marca.isBlank()) {
            this.marca = marca.trim();
        }
        if (modelo != null && !modelo.isBlank()) {
            this.modelo = modelo.trim();
        }
        if (valorReposicion != null) {
            if (valorReposicion.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRuleException("El valor de reposicion debe ser mayor que cero");
            }
            this.valorReposicion = valorReposicion;
        }
        if (ubicacionBodega != null) {
            this.ubicacionBodega = ubicacionBodega.trim();
        }
    }

    public void cambiarTarifaDiaria(BigDecimal nuevaTarifa) {
        validarTarifa(nuevaTarifa);
        this.tarifaDiaria = nuevaTarifa;
        // Se mantiene sincronizada la tarifa DIARIA de la tabla de tarifas.
        buscarTarifa(TipoTarifa.DIARIA).ifPresentOrElse(
                tarifa -> tarifa.actualizarValor(nuevaTarifa),
                () -> agregarTarifa(TarifaAlquiler.de(TipoTarifa.DIARIA, nuevaTarifa)));
    }

    public void cambiarCategoriaPrincipal(Categoria categoria) {
        if (categoria == null) {
            throw new BusinessRuleException("La categoria principal es obligatoria");
        }
        this.categoriaPrincipal = categoria;
        agregarCategoria(categoria);
    }

    // ------------------------------------------------------------------
    // Colecciones del agregado
    // ------------------------------------------------------------------

    public void agregarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new BusinessRuleException("La categoria es obligatoria");
        }
        if (this.categorias == null) {
            this.categorias = new HashSet<>();
        }
        this.categorias.add(categoria);
    }

    public void quitarCategoria(Categoria categoria) {
        if (categoria == null || this.categorias == null) {
            return;
        }
        if (categoria.equals(this.categoriaPrincipal)) {
            throw new BusinessRuleException(
                    "No se puede quitar la categoria principal de la herramienta");
        }
        this.categorias.remove(categoria);
    }

    /**
     * Agrega una foto a la galeria. Si se marca como principal, se le quita esa
     * marca a las demas para que solo exista una portada.
     */
    public void agregarImagen(ImagenHerramienta imagen) {
        if (imagen == null) {
            throw new BusinessRuleException("La imagen es obligatoria");
        }
        if (this.imagenes == null) {
            this.imagenes = new ArrayList<>();
        }
        if (imagen.isPrincipal()) {
            this.imagenes.forEach(ImagenHerramienta::quitarPrincipal);
        } else if (this.imagenes.isEmpty()) {
            // La primera foto se vuelve portada automaticamente.
            imagen.marcarComoPrincipal();
        }
        this.imagenes.add(imagen);
    }

    public Optional<ImagenHerramienta> imagenPrincipal() {
        if (imagenes == null) {
            return Optional.empty();
        }
        return imagenes.stream().filter(ImagenHerramienta::isPrincipal).findFirst();
    }

    public void agregarEspecificacion(EspecificacionTecnica especificacion) {
        if (especificacion == null) {
            throw new BusinessRuleException("La especificacion tecnica es obligatoria");
        }
        if (this.especificaciones == null) {
            this.especificaciones = new ArrayList<>();
        }
        boolean repetida = this.especificaciones.stream()
                .anyMatch(e -> e.getAtributo().equalsIgnoreCase(especificacion.getAtributo()));
        if (repetida) {
            throw new BusinessRuleException(
                    "La herramienta ya tiene registrada la especificacion " + especificacion.getAtributo());
        }
        this.especificaciones.add(especificacion);
    }

    public void agregarTarifa(TarifaAlquiler tarifa) {
        if (tarifa == null) {
            throw new BusinessRuleException("La tarifa es obligatoria");
        }
        if (this.tarifas == null) {
            this.tarifas = new ArrayList<>();
        }
        boolean repetida = this.tarifas.stream()
                .anyMatch(t -> t.getTipoTarifa() == tarifa.getTipoTarifa());
        if (repetida) {
            throw new BusinessRuleException(
                    "La herramienta ya tiene una tarifa de tipo " + tarifa.getTipoTarifa().name());
        }
        this.tarifas.add(tarifa);
    }

    public Optional<TarifaAlquiler> buscarTarifa(TipoTarifa tipo) {
        if (tarifas == null) {
            return Optional.empty();
        }
        return tarifas.stream()
                .filter(t -> t.getTipoTarifa() == tipo && t.isActiva())
                .findFirst();
    }

    /**
     * Calcula cuanto costaria alquilar la herramienta un numero de dias,
     * tomando la tarifa mas conveniente disponible.
     */
    public BigDecimal cotizarPorDias(int dias) {
        if (dias <= 0) {
            throw new BusinessRuleException("La cantidad de dias debe ser mayor que cero");
        }
        if (dias >= TipoTarifa.MENSUAL.getDiasCubiertos()) {
            Optional<TarifaAlquiler> mensual = buscarTarifa(TipoTarifa.MENSUAL);
            if (mensual.isPresent()) {
                return mensual.get().valorPorDia().multiply(BigDecimal.valueOf(dias));
            }
        }
        if (dias >= TipoTarifa.SEMANAL.getDiasCubiertos()) {
            Optional<TarifaAlquiler> semanal = buscarTarifa(TipoTarifa.SEMANAL);
            if (semanal.isPresent()) {
                return semanal.get().valorPorDia().multiply(BigDecimal.valueOf(dias));
            }
        }
        return tarifaDiaria.multiply(BigDecimal.valueOf(dias));
    }

    public boolean perteneceA(Long idProveedorConsultado) {
        return this.idProveedor != null && this.idProveedor.equals(idProveedorConsultado);
    }
}
