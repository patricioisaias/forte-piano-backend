package cl.duoc.fortepiano.controller;

import cl.duoc.fortepiano.dto.*;
import cl.duoc.fortepiano.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de Login, Registro, Sesión Actual y Perfil de Usuario")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiErrorDto.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .error("Unauthorized")
                            .message(e.getMessage())
                            .path("/api/v1/auth/login")
                            .build()
            );
        }
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo cliente con validación Módulo 11")
    public ResponseEntity<?> registro(@Valid @RequestBody RegistroRequest request) {
        try {
            AuthResponse response = authService.registro(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiErrorDto.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .error("Bad Request")
                            .message(e.getMessage())
                            .path("/api/v1/auth/registro")
                            .build()
            );
        }
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener datos de la sesión del usuario autenticado (F5 persistencia)")
    public ResponseEntity<?> obtenerMiSesion(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            SesionDto sesion = authService.obtenerSesion(authentication.getName());
            return ResponseEntity.ok(sesion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiErrorDto.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.NOT_FOUND.value())
                            .error("Not Found")
                            .message(e.getMessage())
                            .path("/api/v1/auth/me")
                            .build()
            );
        }
    }

    @PutMapping("/perfil")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar perfil del usuario autenticado")
    public ResponseEntity<?> actualizarMiPerfil(
            Authentication authentication,
            @RequestBody ActualizarPerfilRequest request
    ) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            SesionDto actualizada = authService.actualizarPerfil(authentication.getName(), request);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiErrorDto.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .error("Bad Request")
                            .message(e.getMessage())
                            .path("/api/v1/auth/perfil")
                            .build()
            );
        }
    }
}
