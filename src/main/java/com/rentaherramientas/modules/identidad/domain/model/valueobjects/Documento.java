package com.rentaherramientas.modules.identidad.domain.model.valueobjects;

import com.rentaherramientas.common.exception.BusinessRuleException;

/**
 * Value Object que agrupa el tipo y el numero de documento.
 *
 * Se modelan juntos porque por separado no significan nada: un "123456" sin saber
 * si es CC o NIT no identifica a nadie. Esta es justamente la idea de un Value
 * Object en DDD.
 */
public record Documento(TipoDocumento tipo, String numero) {

    public Documento {
        if (tipo == null) {
            throw new BusinessRuleException("El tipo de documento es obligatorio");
        }
        if (numero == null || numero.isBlank()) {
            throw new BusinessRuleException("El numero de documento es obligatorio");
        }
        numero = numero.trim().toUpperCase();
        if (numero.length() > 30) {
            throw new BusinessRuleException("El numero de documento no puede superar 30 caracteres");
        }
        // Se permiten digitos, guiones y letras (el NIT colombiano usa digito de verificacion).
        if (!numero.matches("^[A-Z0-9-]+$")) {
            throw new BusinessRuleException(
                    "El numero de documento solo admite letras, numeros y guiones: " + numero);
        }
        if (tipo == TipoDocumento.CC && !numero.matches("^[0-9]+$")) {
            throw new BusinessRuleException("Una cedula de ciudadania solo puede contener digitos");
        }
    }

    @Override
    public String toString() {
        return tipo.name() + " " + numero;
    }
}
