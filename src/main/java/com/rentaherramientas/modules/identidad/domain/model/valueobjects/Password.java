package com.rentaherramientas.modules.identidad.domain.model.valueobjects;

import com.rentaherramientas.common.exception.BusinessRuleException;

/**
 * Value Object que representa la contrasena de un usuario YA CIFRADA.
 *
 * Regla importante del dominio: este objeto nunca guarda la contrasena en texto
 * plano. El metodo {@link #validarPoliticaTextoPlano(String)} se usa para revisar
 * la politica de seguridad ANTES de cifrar, y {@link #deHash(String)} es la unica
 * forma de construir el objeto, recibiendo siempre un hash.
 */
public record Password(String hash) {

    private static final int LONGITUD_MINIMA = 8;

    public Password {
        if (hash == null || hash.isBlank()) {
            throw new BusinessRuleException("La contrasena cifrada es obligatoria");
        }
    }

    /** Unica puerta de entrada: siempre se construye a partir de un hash ya calculado. */
    public static Password deHash(String hash) {
        return new Password(hash);
    }

    /**
     * Valida la politica de contrasenas sobre el texto plano que llega del usuario.
     * No almacena el valor: solo lo revisa y lanza excepcion si no cumple.
     */
    public static void validarPoliticaTextoPlano(String textoPlano) {
        if (textoPlano == null || textoPlano.isBlank()) {
            throw new BusinessRuleException("La contrasena es obligatoria");
        }
        if (textoPlano.length() < LONGITUD_MINIMA) {
            throw new BusinessRuleException(
                    "La contrasena debe tener al menos " + LONGITUD_MINIMA + " caracteres");
        }
        boolean tieneLetra = textoPlano.chars().anyMatch(Character::isLetter);
        boolean tieneDigito = textoPlano.chars().anyMatch(Character::isDigit);
        if (!tieneLetra || !tieneDigito) {
            throw new BusinessRuleException("La contrasena debe combinar al menos una letra y un numero");
        }
    }

    /** Evita que el hash termine impreso por accidente en un log. */
    @Override
    public String toString() {
        return "Password[PROTEGIDA]";
    }
}
