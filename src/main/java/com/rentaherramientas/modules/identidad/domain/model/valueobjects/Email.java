package com.rentaherramientas.modules.identidad.domain.model.valueobjects;

import com.rentaherramientas.common.exception.BusinessRuleException;

import java.util.regex.Pattern;

/**
 * Value Object que representa el correo electronico de un usuario.
 *
 * Al ser un record es inmutable: una vez creado no se puede modificar. La
 * validacion se hace en el constructor compacto, de modo que es imposible tener
 * en memoria un Email con un formato invalido.
 */
public record Email(String valor) {

    private static final Pattern PATRON_EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        if (valor == null || valor.isBlank()) {
            throw new BusinessRuleException("El correo electronico es obligatorio");
        }
        valor = valor.trim().toLowerCase();
        if (valor.length() > 100) {
            throw new BusinessRuleException("El correo electronico no puede superar 100 caracteres");
        }
        if (!PATRON_EMAIL.matcher(valor).matches()) {
            throw new BusinessRuleException("El formato del correo electronico no es valido: " + valor);
        }
    }

    /** Devuelve solo la parte del dominio, util para reportes y validaciones corporativas. */
    public String dominio() {
        return valor.substring(valor.indexOf('@') + 1);
    }

    @Override
    public String toString() {
        return valor;
    }
}
