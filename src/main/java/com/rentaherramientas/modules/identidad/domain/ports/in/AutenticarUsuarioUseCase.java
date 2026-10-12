package com.rentaherramientas.modules.identidad.domain.ports.in;

import com.rentaherramientas.modules.identidad.application.dto.AuthResponseDto;
import com.rentaherramientas.modules.identidad.application.dto.LoginRequest;

/** Puerto de entrada: inicio de sesion y renovacion del token. */
public interface AutenticarUsuarioUseCase {

    AuthResponseDto login(LoginRequest request);

    AuthResponseDto renovarToken(String refreshToken);
}
