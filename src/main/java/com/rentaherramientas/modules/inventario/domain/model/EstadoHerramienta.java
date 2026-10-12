package com.rentaherramientas.modules.inventario.domain.model;

/**
 * Estado fisico de una herramienta dentro del inventario.
 * Coincide con el ENUM de la columna herramientas.estado_operativo.
 */
public enum EstadoHerramienta {

    DISPONIBLE("Lista para ser alquilada"),
    ALQUILADA("Actualmente en poder de un cliente"),
    EN_MANTENIMIENTO("Fuera del catalogo por revision tecnica"),
    FUERA_DE_SERVICIO("Dada de baja, no se puede alquilar");

    private final String descripcion;

    EstadoHerramienta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** Solo una herramienta DISPONIBLE puede entrar en una reserva nueva. */
    public boolean permiteAlquiler() {
        return this == DISPONIBLE;
    }

    /**
     * Define las transiciones validas del inventario. Evita, por ejemplo, que
     * una herramienta dada de baja vuelva a aparecer como disponible sin pasar
     * antes por mantenimiento.
     */
    public boolean puedeTransicionarA(EstadoHerramienta destino) {
        if (destino == null || destino == this) {
            return false;
        }
        return switch (this) {
            case DISPONIBLE -> destino == ALQUILADA
                    || destino == EN_MANTENIMIENTO
                    || destino == FUERA_DE_SERVICIO;
            case ALQUILADA -> destino == DISPONIBLE
                    || destino == EN_MANTENIMIENTO
                    || destino == FUERA_DE_SERVICIO;
            case EN_MANTENIMIENTO -> destino == DISPONIBLE
                    || destino == FUERA_DE_SERVICIO;
            case FUERA_DE_SERVICIO -> destino == EN_MANTENIMIENTO;
        };
    }
}
