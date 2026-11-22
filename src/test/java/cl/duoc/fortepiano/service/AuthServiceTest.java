package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.dto.AuthResponse;
import cl.duoc.fortepiano.dto.LoginRequest;
import cl.duoc.fortepiano.dto.RegistroRequest;
import cl.duoc.fortepiano.dto.SesionDto;
import cl.duoc.fortepiano.entity.Usuario;
import cl.duoc.fortepiano.repository.UsuarioRepository;
import cl.duoc.fortepiano.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RutValidatorService rutValidatorService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = Usuario.builder()
                .run("11111111-1")
                .nombre("Patricio")
                .apellidos("Céspedes")
                .correo("admin@gmail.com")
                .password("$2a$10$encodedPasswordHash")
                .rolId(1)
                .estado("Activo")
                .build();
    }

    @Test
    @DisplayName("Login exitoso con credenciales correctas")
    void testLoginExitoso() {
        LoginRequest request = new LoginRequest("admin@gmail.com", "admin123");

        when(usuarioRepository.findByCorreoIgnoreCase("admin@gmail.com")).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches("admin123", usuarioMock.getPassword())).thenReturn(true);
        when(jwtService.generarToken(usuarioMock)).thenReturn("jwt.token.mock");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertTrue(response.isExito());
        assertEquals("jwt.token.mock", response.getToken());
        assertEquals("Patricio", response.getSesion().getNombre());
        assertEquals(1, response.getSesion().getRolId());
    }

    @Test
    @DisplayName("Login fallido cuando el correo no existe")
    void testLoginCorreoNoExiste() {
        LoginRequest request = new LoginRequest("inexistente@gmail.com", "clave");

        when(usuarioRepository.findByCorreoIgnoreCase("inexistente@gmail.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Login fallido cuando la contraseña es incorrecta")
    void testLoginPasswordIncorrecta() {
        LoginRequest request = new LoginRequest("admin@gmail.com", "claveIncorrecta");

        when(usuarioRepository.findByCorreoIgnoreCase("admin@gmail.com")).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches("claveIncorrecta", usuarioMock.getPassword())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Registro exitoso de un nuevo cliente")
    void testRegistroExitoso() {
        RegistroRequest request = RegistroRequest.builder()
                .run("18765432-7")
                .nombre("Diego")
                .apellidos("Morales")
                .correo("diego@duoc.cl")
                .password("diego123")
                .build();

        when(rutValidatorService.validarRun("18765432-7")).thenReturn(true);
        when(rutValidatorService.validarDominioCorreo("diego@duoc.cl")).thenReturn(true);
        when(usuarioRepository.existsByRun("18765432-7")).thenReturn(false);
        when(usuarioRepository.existsByCorreoIgnoreCase("diego@duoc.cl")).thenReturn(false);
        when(passwordEncoder.encode("diego123")).thenReturn("$2a$10$encodedHash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generarToken(any(Usuario.class))).thenReturn("jwt.token.registro");

        AuthResponse response = authService.registro(request);

        assertNotNull(response);
        assertTrue(response.isExito());
        assertEquals("jwt.token.registro", response.getToken());
        assertEquals(3, response.getSesion().getRolId()); // Rol Cliente por defecto
    }

    @Test
    @DisplayName("Registro rechazado cuando el RUN no cumple Módulo 11")
    void testRegistroRunInvalido() {
        RegistroRequest request = RegistroRequest.builder()
                .run("11111111-9")
                .correo("usuario@gmail.com")
                .build();

        when(rutValidatorService.validarRun("11111111-9")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.registro(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener sesión actual por correo")
    void testObtenerSesion() {
        when(usuarioRepository.findByCorreoIgnoreCase("admin@gmail.com")).thenReturn(Optional.of(usuarioMock));

        SesionDto sesion = authService.obtenerSesion("admin@gmail.com");

        assertNotNull(sesion);
        assertEquals("Patricio", sesion.getNombre());
        assertEquals("Administrador", sesion.getRolNombre());
    }
}
