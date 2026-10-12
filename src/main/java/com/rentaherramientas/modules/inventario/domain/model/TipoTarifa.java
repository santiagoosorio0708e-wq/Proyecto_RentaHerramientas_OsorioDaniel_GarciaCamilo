package com.rentaherramientas.modules.inventario.domain.model;

/**
 * Modalidades de cobro de una herramienta.
 *
 * Tener varias tarifas por herramienta es justamente lo que obliga a sacarlas
 * a su propia tabla (tarifas_herramienta) en la normalizacion a 4FN.
 */
public enum TipoTarifa {

    DIARIA("Cobro por dia calendario", 1),
    FIN_DE_SEMANA("Tarifa especial de sabado y domingo", 2),
    SEMANAL("Cobro por semana completa", 7),
    MENSUAL("Cobro por mes de 30 dias", 30);

    private final String descripcion;
    private final int diasCubiertos;

    TipoTarifa(String descripcion, int diasCubiertos) {
        this.descripcion = descripcion;
        this.diasCubiertos = diasCubiertos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getDiasCubiertos() {
        return diasCubiertos;
    }
}
