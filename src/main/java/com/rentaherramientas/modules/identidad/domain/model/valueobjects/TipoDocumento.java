package com.rentaherramientas.modules.identidad.domain.model.valueobjects;

/**
 * Tipos de documento aceptados por la plataforma.
 * Coincide exactamente con el ENUM de la columna usuarios.tipo_documento.
 */
public enum TipoDocumento {

    CC("Cedula de ciudadania"),
    NIT("Numero de identificacion tributaria"),
    CE("Cedula de extranjeria"),
    PASAPORTE("Pasaporte");

    private final String descripcion;

    TipoDocumento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** Un proveedor normalmente es una empresa, por eso se identifica con NIT. */
    public boolean esEmpresarial() {
        return this == NIT;
    }
}
