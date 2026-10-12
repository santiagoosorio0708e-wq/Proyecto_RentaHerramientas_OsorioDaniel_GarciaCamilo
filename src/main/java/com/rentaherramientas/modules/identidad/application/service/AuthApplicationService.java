package com.rentaherramientas.modules.identidad.application.service;

import com.rentaherramientas.common.exception.BusinessRuleException;
import com.rentaherramientas.common.exception.ResourceNotFoundException;
import com.rentaherramientas.modules.identidad.application.dto.AuthResponseDto;
import com.rentaherramientas.modules.identidad.application.dto.LoginRequest;
import com.rentaherramientas.modules.identidad.application.dto.RegistroClienteCommand;
import com.rentaherramientas.modules.identidad.application.dto.RegistroProveedorCommand;
import com.rentaherramientas.modules.identidad.domain.model.PerfilCliente;
import com.rentaherramientas.modules.identidad.domain.model.PerfilProveedor;
import com.rentaherramientas.modules.identidad.domain.model.Rol;
import com.rentaherramientas.modules.identidad.domain.model.Usuario;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Documento;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Email;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Password;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.TipoDocumento;
import com.rentaherramientas.modules.identidad.domain.ports.in.AutenticarUsuarioUseCase;
import com.rentaherramientas.modules.identidad.domain.ports.in.RegistrarUsuarioUseCase;
import com.rentaherramientas.modules.identidad.domain.ports.out.PasswordEncoderPort;
import com.rentaherramientas.modules.identidad.domain.ports.out.RolRepositoryPort;
import com.rentaherramientas.modules.identidad.domain.ports.out.TokenProviderPort;
import com.rentaherramientas.modules.identidad.domain.ports.out.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Servicio de aplicacion del bounded context auth-usuarios.
 *
 * Orquesta los casos de uso de registro y autenticacion. No contiene reglas de
 * negocio propias: las reglas viven en el dominio (Usuario, Email, Password) y
 * aqui solo se coordina el orden de los pasos y la transaccion.
 */
@Service
@RequiredArgsConstructor
public class AuthApplicationService implements RegistrarUsuarioUseCase, AutenticarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final RolRepositoryPort rolRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    private static final String TIPO_TOKEN = "Bearer";

    // ------------------------------------------------------------------
    // CASO DE USO: Registro de cliente
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public Usuario registrarCliente(RegistroClienteCommand command) {

        Email email = new Email(command.email());
        Documento documento = new Documento(
                TipoDocumento.valueOf(command.tipoDocumento().toUpperCase()),
                command.numeroDocumento());

        validarUnicidad(email, documento);

        Password.validarPoliticaTextoPlano(command.password());
        Password passwordCifrada = passwordEncoder.cifrar(command.password());

        Usuario usuario = Usuario.registrar(
                command.nombreCompleto(), email, passwordCifrada,
                command.telefono(), command.direccion(), documento);

        usuario.asignarRol(obtenerRol(Rol.CLIENTE));

        PerfilCliente perfil = PerfilCliente.builder()
                .tipoCliente(command.tipoCliente() == null || command.tipoCliente().isBlank()
                        ? PerfilCliente.PARTICULAR
                        : command.tipoCliente().toUpperCase())
                .ciudadResidencia(command.ciudadResidencia())
                .cupoCredito(command.cupoCredito() == null ? BigDecimal.ZERO : command.cupoCredito())
                .fechaRegistro(LocalDate.now())
                .alquileresRealizados(0)
                .build();

        usuario.asignarPerfilCliente(perfil);

        return usuarioRepository.guardar(usuario);
    }

    // ------------------------------------------------------------------
    // CASO DE USO: Registro de proveedor
    // ------------------------------------------------------------------
    @Override
    @Transactional
    public Usuario registrarProveedor(RegistroProveedorCommand command) {

        Email email = new Email(command.email());
        Documento documento = new Documento(
                TipoDocumento.valueOf(command.tipoDocumento().toUpperCase()),
                command.numeroDocumento());

        validarUnicidad(email, documento);

        Password.validarPoliticaTextoPlano(command.password());
        Password passwordCifrada = passwordEncoder.cifrar(command.password());

        Usuario usuario = Usuario.registrar(
                command.nombreCompleto(), email, passwordCifrada,
                command.telefono(), command.direccion(), documento);

        usuario.asignarRol(obtenerRol(Rol.PROVEEDOR));

        PerfilProveedor perfil = PerfilProveedor.builder()
                .razonSocial(command.razonSocial())
                .nombreComercial(command.nombreComercial())
                .ciudadOperacion(command.ciudadOperacion())
                .sitioWeb(command.sitioWeb())
                .fechaVinculacion(LocalDate.now())
                // Un proveedor nuevo nace SIN verificar: el administrador debe
                // revisar su documentacion antes de que pueda publicar herramientas.
                .verificado(false)
                .calificacionPromedio(0)
                .build();

        usuario.asignarPerfilProveedor(perfil);

        return usuarioRepository.guardar(usuario);
    }

    // ------------------------------------------------------------------
    // CASO DE USO: Login
    // ------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequest request) {

        Email email = new Email(request.email());

        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                // Mensaje generico a proposito: no se revela si el correo existe o no.
                .orElseThrow(() -> new BusinessRuleException("Credenciales invalidas"));

        if (!passwordEncoder.coincide(request.password(), usuario.getPassword())) {
            throw new BusinessRuleException("Credenciales invalidas");
        }

        if (!usuario.isActivo()) {
            throw new BusinessRuleException(
                    "La cuenta se encuentra bloqueada. Comuniquese con el administrador.");
        }

        return construirRespuesta(usuario);
    }

    // ------------------------------------------------------------------
    // CASO DE USO: Renovacion de token
    // ------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto renovarToken(String refreshToken) {

        if (!tokenProvider.esTokenValido(refreshToken)) {
            throw new BusinessRuleException("El refresh token es invalido o ya expiro");
        }
        if (!tokenProvider.esRefreshToken(refreshToken)) {
            throw new BusinessRuleException("El token enviado no es un refresh token");
        }

        String email = tokenProvider.extraerEmail(refreshToken);
        Usuario usuario = usuarioRepository.buscarPorEmail(new Email(email))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe el usuario asociado al token: " + email));

        if (!usuario.isActivo()) {
            throw new BusinessRuleException("La cuenta se encuentra bloqueada");
        }

        return construirRespuesta(usuario);
    }

    // ------------------------------------------------------------------
    // Metodos de apoyo
    // ------------------------------------------------------------------
    private void validarUnicidad(Email email, Documento documento) {
        if (usuarioRepository.existePorEmail(email)) {
            throw new BusinessRuleException(
                    "Ya existe una cuenta registrada con el correo " + email.valor());
        }
        if (usuarioRepository.existePorDocumento(documento)) {
            throw new BusinessRuleException(
                    "Ya existe una cuenta registrada con el documento " + documento.numero());
        }
    }

    private Rol obtenerRol(String nombreRol) {
        return rolRepository.buscarPorNombre(nombreRol)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El rol " + nombreRol + " no existe. Ejecute el script de datos iniciales."));
    }

    private AuthResponseDto construirRespuesta(Usuario usuario) {
        return AuthResponseDto.builder()
                .accessToken(tokenProvider.generarAccessToken(usuario))
                .refreshToken(tokenProvider.generarRefreshToken(usuario))
                .tipoToken(TIPO_TOKEN)
                .expiraEnSegundos(tokenProvider.segundosDeVigenciaAccessToken())
                .idUsuario(usuario.getIdUsuario())
                .nombreCompleto(usuario.getNombreCompleto())
                .email(usuario.getEmail().valor())
                .roles(usuario.nombresDeRoles())
                .build();
    }
}
