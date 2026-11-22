package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.dto.*;
import cl.duoc.fortepiano.entity.Usuario;
import cl.duoc.fortepiano.repository.UsuarioRepository;
import cl.duoc.fortepiano.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RutValidatorService rutValidatorService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(request.getCorreo())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas: correo no registrado."));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas: contraseña incorrecta.");
        }

        if (!"Activo".equalsIgnoreCase(usuario.getEstado())) {
            throw new IllegalArgumentException("La cuenta se encuentra inactiva. Comuníquese con administración.");
        }

        String token = jwtService.generarToken(usuario);

        return AuthResponse.builder()
                .exito(true)
                .token(token)
                .mensaje("Inicio de sesión exitoso.")
                .sesion(mapearSesion(usuario))
                .build();
    }

    @Transactional
    public AuthResponse registro(RegistroRequest request) {
        if (!rutValidatorService.validarRun(request.getRun())) {
            throw new IllegalArgumentException("El RUN '" + request.getRun() + "' no es válido según algoritmo Módulo 11.");
        }

        if (!rutValidatorService.validarDominioCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("El correo debe pertenecer a los dominios autorizados (@duoc.cl, @profesor.duoc.cl, @gmail.com).");
        }

        if (usuarioRepository.existsByRun(request.getRun())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con el RUN: " + request.getRun());
        }

        if (usuarioRepository.existsByCorreoIgnoreCase(request.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con el correo: " + request.getCorreo());
        }

        Usuario nuevoUsuario = Usuario.builder()
                .run(request.getRun())
                .nombre(request.getNombre())
                .apellidos(request.getApellidos())
                .correo(request.getCorreo().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .fechaNacimiento(request.getFechaNacimiento())
                .rolId(3) // Cliente por defecto
                .regionId(request.getRegionId())
                .comunaId(request.getComunaId())
                .direccion(request.getDireccion())
                .estado("Activo")
                .build();

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        String token = jwtService.generarToken(guardado);

        return AuthResponse.builder()
                .exito(true)
                .token(token)
                .mensaje("Registro de usuario exitoso.")
                .sesion(mapearSesion(guardado))
                .build();
    }

    public SesionDto obtenerSesion(String correo) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con correo: " + correo));
        return mapearSesion(usuario);
    }

    @Transactional
    public SesionDto actualizarPerfil(String correoActual, ActualizarPerfilRequest request) {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correoActual)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con correo: " + correoActual));

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            usuario.setNombre(request.getNombre());
        }
        if (request.getApellidos() != null && !request.getApellidos().isBlank()) {
            usuario.setApellidos(request.getApellidos());
        }
        if (request.getCorreo() != null && !request.getCorreo().equalsIgnoreCase(correoActual)) {
            if (!rutValidatorService.validarDominioCorreo(request.getCorreo())) {
                throw new IllegalArgumentException("El correo debe pertenecer a los dominios autorizados (@duoc.cl, @profesor.duoc.cl, @gmail.com).");
            }
            if (usuarioRepository.existsByCorreoIgnoreCase(request.getCorreo())) {
                throw new IllegalArgumentException("El correo ingresado ya está registrado por otro usuario.");
            }
            usuario.setCorreo(request.getCorreo().trim());
        }
        if (request.getRegionId() != null) {
            usuario.setRegionId(request.getRegionId());
        }
        if (request.getComunaId() != null) {
            usuario.setComunaId(request.getComunaId());
        }
        if (request.getDireccion() != null) {
            usuario.setDireccion(request.getDireccion());
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return mapearSesion(actualizado);
    }

    public SesionDto mapearSesion(Usuario usuario) {
        String rolNombre = switch (usuario.getRolId() != null ? usuario.getRolId() : 3) {
            case 1 -> "Administrador";
            case 2 -> "Vendedor";
            default -> "Cliente";
        };

        return SesionDto.builder()
                .run(usuario.getRun())
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidos())
                .correo(usuario.getCorreo())
                .rolId(usuario.getRolId())
                .rolNombre(rolNombre)
                .build();
    }
}
