package com.rentaherramientas.modules.identidad.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Datos que envia el formulario de registro de un CLIENTE. */
@Schema(description = "Datos necesarios para registrar un nuevo cliente en la plataforma")
public record RegistroClienteCommand(

        @Schema(description = "Nombre completo o razon social del cliente", example = "Juan Camilo Garcia")
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
        String nombreCompleto,

        @Schema(description = "Correo electronico, se usa como usuario de inicio de sesion", example = "cliente.nuevo@gmail.com")
        @NotBlank(message = "El correo electronico es obligatorio")
        @Email(message = "El formato del correo electronico no es valido")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres")
        String email,

        @Schema(description = "Contrasena: minimo 8 caracteres, con letras y numeros", example = "Password123")
        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
        String password,

        @Schema(description = "Telefono de contacto", example = "3185550109")
        @NotBlank(message = "El telefono es obligatorio")
        @Pattern(regexp = "^[0-9+\\s-]{7,20}$", message = "El telefono tiene un formato invalido")
        String telefono,

        @Schema(description = "Direccion fisica", example = "Carrera 33 # 48-19, Bucaramanga")
        @NotBlank(message = "La direccion es obligatoria")
        @Size(max = 180, message = "La direccion no puede superar 180 caracteres")
        String direccion,

        @Schema(description = "Tipo de documento: CC, NIT, CE o PASAPORTE", example = "CC")
        @NotBlank(message = "El tipo de documento es obligatorio")
        @Pattern(regexp = "CC|NIT|CE|PASAPORTE", message = "El tipo de documento debe ser CC, NIT, CE o PASAPORTE")
        String tipoDocumento,

        @Schema(description = "Numero de documento, unico en el sistema", example = "1098765433")
        @NotBlank(message = "El numero de documento es obligatorio")
        @Size(max = 30, message = "El numero de documento no puede superar 30 caracteres")
        String numeroDocumento,

        @Schema(description = "Tipo de cliente: PARTICULAR o EMPRESARIAL", example = "PARTICULAR")
        @Pattern(regexp = "PARTICULAR|EMPRESARIAL", message = "El tipo de cliente debe ser PARTICULAR o EMPRESARIAL")
        String tipoCliente,

        @Schema(description = "Ciudad de residencia", example = "Bucaramanga")
        @Size(max = 80, message = "La ciudad no puede superar 80 caracteres")
        String ciudadResidencia,

        @Schema(description = "Cupo de credito solicitado (opcional)", example = "0")
        @PositiveOrZero(message = "El cupo de credito no puede ser negativo")
        BigDecimal cupoCredito
) {
}
