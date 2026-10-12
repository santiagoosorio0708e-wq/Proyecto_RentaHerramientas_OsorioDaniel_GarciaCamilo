package com.rentaherramientas.modules.identidad.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos que envia el formulario de registro de un PROVEEDOR. */
@Schema(description = "Datos necesarios para registrar un nuevo proveedor de herramientas")
public record RegistroProveedorCommand(

        @Schema(description = "Nombre del representante o de la empresa", example = "Maquinaria y Andamios Occidente")
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
        String nombreCompleto,

        @Schema(description = "Correo corporativo, se usa como usuario de inicio de sesion", example = "ventas@maquiocc.com")
        @NotBlank(message = "El correo electronico es obligatorio")
        @Email(message = "El formato del correo electronico no es valido")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres")
        String email,

        @Schema(description = "Contrasena: minimo 8 caracteres, con letras y numeros", example = "Password123")
        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
        String password,

        @Schema(description = "Telefono de contacto", example = "3135550104")
        @NotBlank(message = "El telefono es obligatorio")
        @Pattern(regexp = "^[0-9+\\s-]{7,20}$", message = "El telefono tiene un formato invalido")
        String telefono,

        @Schema(description = "Direccion de la bodega o sede principal", example = "Calle 50 # 45-30, Medellin")
        @NotBlank(message = "La direccion es obligatoria")
        @Size(max = 180, message = "La direccion no puede superar 180 caracteres")
        String direccion,

        @Schema(description = "Tipo de documento: normalmente NIT para proveedores", example = "NIT")
        @NotBlank(message = "El tipo de documento es obligatorio")
        @Pattern(regexp = "CC|NIT|CE|PASAPORTE", message = "El tipo de documento debe ser CC, NIT, CE o PASAPORTE")
        String tipoDocumento,

        @Schema(description = "Numero de documento o NIT, unico en el sistema", example = "900987654-2")
        @NotBlank(message = "El numero de documento es obligatorio")
        @Size(max = 30, message = "El numero de documento no puede superar 30 caracteres")
        String numeroDocumento,

        @Schema(description = "Razon social registrada en camara de comercio", example = "Maquinaria y Andamios Occidente S.A.S.")
        @NotBlank(message = "La razon social es obligatoria para un proveedor")
        @Size(max = 150, message = "La razon social no puede superar 150 caracteres")
        String razonSocial,

        @Schema(description = "Nombre comercial con el que opera", example = "MaquiOccidente")
        @Size(max = 120, message = "El nombre comercial no puede superar 120 caracteres")
        String nombreComercial,

        @Schema(description = "Ciudad principal de operacion", example = "Medellin")
        @NotBlank(message = "La ciudad de operacion es obligatoria")
        @Size(max = 80, message = "La ciudad no puede superar 80 caracteres")
        String ciudadOperacion,

        @Schema(description = "Sitio web corporativo (opcional)", example = "https://www.maquiocc.com")
        @Size(max = 150, message = "El sitio web no puede superar 150 caracteres")
        String sitioWeb
) {
}
