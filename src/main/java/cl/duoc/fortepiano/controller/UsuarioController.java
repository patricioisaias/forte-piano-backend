package cl.duoc.fortepiano.controller;

import cl.duoc.fortepiano.dto.ApiErrorDto;
import cl.duoc.fortepiano.entity.Usuario;
import cl.duoc.fortepiano.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión CRUD de usuarios y cuentas de acceso")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios registrados")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{run}")
    @Operation(summary = "Buscar usuario por su RUN")
    public ResponseEntity<?> obtenerPorRun(@PathVariable String run) {
        return usuarioService.buscarPorRun(run)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiErrorDto.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message("No se encontró usuario con el RUN: " + run)
                                .path("/api/v1/usuarios/" + run)
                                .build()
                ));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario con validación Módulo 11")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario creado = usuarioService.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiErrorDto.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .error("Bad Request")
                            .message(e.getMessage())
                            .path("/api/v1/usuarios")
                            .build()
            );
        }
    }

    @PutMapping("/{run}")
    @Operation(summary = "Actualizar información de un usuario")
    public ResponseEntity<?> actualizarUsuario(@PathVariable String run, @RequestBody Usuario datos) {
        try {
            Usuario actualizado = usuarioService.actualizarUsuario(run, datos);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            HttpStatus status = e.getMessage().contains("No se encontró") ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(
                    ApiErrorDto.builder()
                            .timestamp(LocalDateTime.now())
                            .status(status.value())
                            .error(status.getReasonPhrase())
                            .message(e.getMessage())
                            .path("/api/v1/usuarios/" + run)
                            .build()
            );
        }
    }

    @DeleteMapping("/{run}")
    @Operation(summary = "Eliminar un usuario por su RUN")
    public ResponseEntity<?> eliminarUsuario(@PathVariable String run) {
        try {
            usuarioService.eliminarUsuario(run);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiErrorDto.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.NOT_FOUND.value())
                            .error("Not Found")
                            .message(e.getMessage())
                            .path("/api/v1/usuarios/" + run)
                            .build()
            );
        }
    }
}
