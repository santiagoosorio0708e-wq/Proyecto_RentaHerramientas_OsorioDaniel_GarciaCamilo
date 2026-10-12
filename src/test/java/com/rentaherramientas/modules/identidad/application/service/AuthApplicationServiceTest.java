package com.rentaherramientas.modules.identidad.application.service;

import com.rentaherramientas.common.exception.BusinessRuleException;
import com.rentaherramientas.modules.identidad.application.dto.AuthResponseDto;
import com.rentaherramientas.modules.identidad.application.dto.LoginRequest;
import com.rentaherramientas.modules.identidad.application.dto.RegistroClienteCommand;
import com.rentaherramientas.modules.identidad.application.dto.RegistroProveedorCommand;
import com.rentaherramientas.modules.identidad.domain.model.Rol;
import com.rentaherramientas.modules.identidad.domain.model.Usuario;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Documento;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Email;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.Password;
import com.rentaherramientas.modules.identidad.domain.model.valueobjects.TipoDocumento;
import com.rentaherramientas.modules.identidad.domain.ports.out.PasswordEncoderPort;
import com.rentaherramientas.modules.identidad.domain.ports.out.RolRepositoryPort;
import com.rentaherramientas.modules.identidad.domain.ports.out.TokenProviderPort;
import com.rentaherramientas.modules.identidad.domain.ports.out.UsuarioRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del servicio de autenticacion.
 *
 * Son unitarias de verdad: no levantan Spring ni tocan la base de datos. Los
 * puertos se reemplazan por mocks, que es justamente la ventaja de haber
 * definido el dominio contra interfaces y no contra clases concretas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthApplicationService - registro y autenticacion")
class AuthApplicationServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private RolRepositoryPort rolRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private TokenProviderPort tokenProvider;

    @InjectMocks
    private AuthApplicationService authService;

    private RegistroClienteCommand comandoCliente;
    private RegistroProveedorCommand comandoProveedor;
    private Rol rolCliente;
    private Rol rolProveedor;

    @BeforeEach
    void setUp() {
        comandoCliente = new RegistroClienteCommand(
                "Juan Camilo Garcia",
                "camilo.nuevo@gmail.com",
                "Password123",
                "3185550109",
                "Carrera 33 # 48-19, Bucaramanga",
                "CC",
                "1098765499",
                "PARTICULAR",
                "Bucaramanga",
                BigDecimal.ZERO);

        comandoProveedor = new RegistroProveedorCommand(
                "Maquinaria del Oriente SAS",
                "ventas@maquioriente.com",
                "Password123",
                "3175550108",
                "Calle 50 # 45-30, Bucaramanga",
                "NIT",
                "901999888-7",
                "Maquinaria del Oriente S.A.S.",
                "MaquiOriente",
                "Bucaramanga",
                "https://www.maquioriente.com");

        rolCliente = Rol.builder().idRol(3L).nombre(Rol.CLIENTE).descripcion("Cliente").build();
        rolProveedor = Rol.builder().idRol(2L).nombre(Rol.PROVEEDOR).descripcion("Proveedor").build();
    }

    // ------------------------------------------------------------------
    // Registro
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Registra un cliente, le asigna ROLE_CLIENTE y crea su perfil")
    void registraClienteCorrectamente() {
        when(usuarioRepository.existePorEmail(any(Email.class))).thenReturn(false);
        when(usuarioRepository.existePorDocumento(any(Documento.class))).thenReturn(false);
        when(passwordEncoder.cifrar(anyString())).thenReturn(Password.deHash("$2b$10$hashSimulado"));
        when(rolRepository.buscarPorNombre(Rol.CLIENTE)).thenReturn(Optional.of(rolCliente));
        when(usuarioRepository.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = authService.registrarCliente(comandoCliente);

        assertNotNull(resultado);
        assertTrue(resultado.esCliente(), "El usuario debe quedar con rol CLIENTE");
        assertNotNull(resultado.getPerfilCliente(), "Debe crearse el perfil de cliente");
        assertEquals("PARTICULAR", resultado.getPerfilCliente().getTipoCliente());
        assertTrue(resultado.isActivo(), "Una cuenta nueva nace activa");
        verify(usuarioRepository).guardar(any(Usuario.class));
    }

    @Test
    @DisplayName("Un proveedor recien registrado queda SIN verificar")
    void proveedorNuevoNoQuedaVerificado() {
        when(usuarioRepository.existePorEmail(any(Email.class))).thenReturn(false);
        when(usuarioRepository.existePorDocumento(any(Documento.class))).thenReturn(false);
        when(passwordEncoder.cifrar(anyString())).thenReturn(Password.deHash("$2b$10$hashSimulado"));
        when(rolRepository.buscarPorNombre(Rol.PROVEEDOR)).thenReturn(Optional.of(rolProveedor));
        when(usuarioRepository.guardar(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = authService.registrarProveedor(comandoProveedor);

        assertTrue(resultado.esProveedor());
        assertNotNull(resultado.getPerfilProveedor());
        assertEquals(false, resultado.getPerfilProveedor().isVerificado(),
                "El administrador debe verificarlo antes de que pueda publicar");
        assertEquals(false, resultado.getPerfilProveedor().puedePublicarHerramientas());
    }

    @Test
    @DisplayName("No permite registrar dos cuentas con el mismo correo")
    void rechazaCorreoDuplicado() {
        when(usuarioRepository.existePorEmail(any(Email.class))).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> authService.registrarCliente(comandoCliente));

        assertTrue(ex.getMessage().toLowerCase().contains("correo"));
        verify(usuarioRepository, never()).guardar(any(Usuario.class));
    }

    @Test
    @DisplayName("No permite registrar dos cuentas con el mismo documento")
    void rechazaDocumentoDuplicado() {
        when(usuarioRepository.existePorEmail(any(Email.class))).thenReturn(false);
        when(usuarioRepository.existePorDocumento(any(Documento.class))).thenReturn(true);

        assertThrows(BusinessRuleException.class,
                () -> authService.registrarCliente(comandoCliente));

        verify(usuarioRepository, never()).guardar(any(Usuario.class));
    }

    @Test
    @DisplayName("Rechaza una contrasena que no cumple la politica de seguridad")
    void rechazaPasswordDebil() {
        RegistroClienteCommand comandoDebil = new RegistroClienteCommand(
                "Juan Camilo Garcia", "otro.correo@gmail.com", "abcdefgh",
                "3185550109", "Carrera 33 # 48-19", "CC", "1098765498",
                "PARTICULAR", "Bucaramanga", BigDecimal.ZERO);

        when(usuarioRepository.existePorEmail(any(Email.class))).thenReturn(false);
        when(usuarioRepository.existePorDocumento(any(Documento.class))).thenReturn(false);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> authService.registrarCliente(comandoDebil));

        assertTrue(ex.getMessage().contains("letra"));
    }

    // ------------------------------------------------------------------
    // Login
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Devuelve access token y refresh token cuando las credenciales son correctas")
    void loginExitoso() {
        Usuario usuario = usuarioDePrueba(true);

        when(usuarioRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(usuario));
        when(passwordEncoder.coincide(anyString(), any(Password.class))).thenReturn(true);
        when(tokenProvider.generarAccessToken(usuario)).thenReturn("access-token-simulado");
        when(tokenProvider.generarRefreshToken(usuario)).thenReturn("refresh-token-simulado");
        when(tokenProvider.segundosDeVigenciaAccessToken()).thenReturn(86400L);

        AuthResponseDto respuesta = authService.login(
                new LoginRequest("camilo.garcia@gmail.com", "Password123"));

        assertEquals("access-token-simulado", respuesta.accessToken());
        assertEquals("refresh-token-simulado", respuesta.refreshToken());
        assertEquals("Bearer", respuesta.tipoToken());
        assertEquals(86400L, respuesta.expiraEnSegundos());
        assertTrue(respuesta.roles().contains(Rol.CLIENTE));
    }

    @Test
    @DisplayName("Rechaza el login si la contrasena no coincide")
    void loginConPasswordIncorrecta() {
        when(usuarioRepository.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(usuarioDePrueba(true)));
        when(passwordEncoder.coincide(anyString(), any(Password.class))).thenReturn(false);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> authService.login(new LoginRequest("camilo.garcia@gmail.com", "claveMala1")));

        assertEquals("Credenciales invalidas", ex.getMessage());
    }

    @Test
    @DisplayName("No revela si el correo existe cuando el usuario no esta registrado")
    void loginConCorreoInexistenteDaMensajeGenerico() {
        when(usuarioRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> authService.login(new LoginRequest("noexiste@gmail.com", "Password123")));

        // Mismo mensaje que cuando la contrasena falla: no se filtra informacion.
        assertEquals("Credenciales invalidas", ex.getMessage());
    }

    @Test
    @DisplayName("Impide iniciar sesion a una cuenta bloqueada")
    void loginConCuentaBloqueada() {
        when(usuarioRepository.buscarPorEmail(any(Email.class)))
                .thenReturn(Optional.of(usuarioDePrueba(false)));
        when(passwordEncoder.coincide(anyString(), any(Password.class))).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> authService.login(new LoginRequest("camilo.garcia@gmail.com", "Password123")));

        assertTrue(ex.getMessage().toLowerCase().contains("bloqueada"));
    }

    // ------------------------------------------------------------------
    // Refresh token
    // ------------------------------------------------------------------

    @Test
    @DisplayName("No acepta un access token en el endpoint de refresco")
    void rechazaAccessTokenComoRefresh() {
        when(tokenProvider.esTokenValido("token-de-acceso")).thenReturn(true);
        when(tokenProvider.esRefreshToken("token-de-acceso")).thenReturn(false);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> authService.renovarToken("token-de-acceso"));

        assertTrue(ex.getMessage().contains("refresh token"));
    }

    @Test
    @DisplayName("Rechaza un refresh token expirado o alterado")
    void rechazaRefreshTokenInvalido() {
        when(tokenProvider.esTokenValido("token-vencido")).thenReturn(false);

        assertThrows(BusinessRuleException.class,
                () -> authService.renovarToken("token-vencido"));
    }

    // ------------------------------------------------------------------
    // Utilidad
    // ------------------------------------------------------------------

    private Usuario usuarioDePrueba(boolean activo) {
        Usuario usuario = Usuario.builder()
                .idUsuario(9L)
                .nombreCompleto("Juan Camilo Garcia")
                .email(new Email("camilo.garcia@gmail.com"))
                .password(Password.deHash("$2b$10$hashSimulado"))
                .telefono("3185550109")
                .direccion("Carrera 33 # 48-19, Bucaramanga")
                .documento(new Documento(TipoDocumento.CC, "1098765433"))
                .activo(activo)
                .build();
        usuario.asignarRol(rolCliente);
        return usuario;
    }
}
