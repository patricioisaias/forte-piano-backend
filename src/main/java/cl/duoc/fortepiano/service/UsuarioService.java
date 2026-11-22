package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.entity.Usuario;
import cl.duoc.fortepiano.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RutValidatorService rutValidatorService;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorRun(String run) {
        String runLimpio = rutValidatorService.limpiarRun(run);
        // Buscar por RUN tal cual o buscar con formato estándar
        return usuarioRepository.findById(run)
                .or(() -> usuarioRepository.findAll().stream()
                        .filter(u -> rutValidatorService.limpiarRun(u.getRun()).equalsIgnoreCase(runLimpio))
                        .findFirst());
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo);
    }

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        if (!rutValidatorService.validarRun(usuario.getRun())) {
            throw new IllegalArgumentException("El RUN ingresado '" + usuario.getRun() + "' no es válido según algoritmo Módulo 11.");
        }

        if (!rutValidatorService.validarDominioCorreo(usuario.getCorreo())) {
            throw new IllegalArgumentException("El correo '" + usuario.getCorreo() + "' debe pertenecer a los dominios autorizados (@duoc.cl, @profesor.duoc.cl, @gmail.com).");
        }

        if (usuarioRepository.existsByRun(usuario.getRun())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con el RUN: " + usuario.getRun());
        }

        if (usuarioRepository.existsByCorreoIgnoreCase(usuario.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con el correo: " + usuario.getCorreo());
        }

        if (usuario.getEstado() == null || usuario.getEstado().isBlank()) {
            usuario.setEstado("Activo");
        }

        if (usuario.getRolId() == null) {
            usuario.setRolId(3); // Cliente por defecto
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizarUsuario(String run, Usuario datosActualizados) {
        Usuario usuarioExistente = buscarPorRun(run)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró usuario con el RUN: " + run));

        if (datosActualizados.getNombre() != null) {
            usuarioExistente.setNombre(datosActualizados.getNombre());
        }
        if (datosActualizados.getApellidos() != null) {
            usuarioExistente.setApellidos(datosActualizados.getApellidos());
        }
        if (datosActualizados.getCorreo() != null && !datosActualizados.getCorreo().equalsIgnoreCase(usuarioExistente.getCorreo())) {
            if (!rutValidatorService.validarDominioCorreo(datosActualizados.getCorreo())) {
                throw new IllegalArgumentException("El correo debe pertenecer a los dominios autorizados (@duoc.cl, @profesor.duoc.cl, @gmail.com).");
            }
            if (usuarioRepository.existsByCorreoIgnoreCase(datosActualizados.getCorreo())) {
                throw new IllegalArgumentException("El correo ya está en uso por otro usuario.");
            }
            usuarioExistente.setCorreo(datosActualizados.getCorreo());
        }
        if (datosActualizados.getPassword() != null && !datosActualizados.getPassword().isBlank()) {
            usuarioExistente.setPassword(datosActualizados.getPassword());
        }
        if (datosActualizados.getFechaNacimiento() != null) {
            usuarioExistente.setFechaNacimiento(datosActualizados.getFechaNacimiento());
        }
        if (datosActualizados.getRolId() != null) {
            usuarioExistente.setRolId(datosActualizados.getRolId());
        }
        if (datosActualizados.getRegionId() != null) {
            usuarioExistente.setRegionId(datosActualizados.getRegionId());
        }
        if (datosActualizados.getComunaId() != null) {
            usuarioExistente.setComunaId(datosActualizados.getComunaId());
        }
        if (datosActualizados.getDireccion() != null) {
            usuarioExistente.setDireccion(datosActualizados.getDireccion());
        }
        if (datosActualizados.getEstado() != null) {
            usuarioExistente.setEstado(datosActualizados.getEstado());
        }

        return usuarioRepository.save(usuarioExistente);
    }

    @Transactional
    public void eliminarUsuario(String run) {
        Usuario usuario = buscarPorRun(run)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró usuario con el RUN: " + run));
        usuarioRepository.delete(usuario);
    }
}
