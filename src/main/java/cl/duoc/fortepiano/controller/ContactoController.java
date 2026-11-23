package cl.duoc.fortepiano.controller;

import cl.duoc.fortepiano.dto.ApiErrorDto;
import cl.duoc.fortepiano.dto.MensajeContactoRequest;
import cl.duoc.fortepiano.entity.MensajeContacto;
import cl.duoc.fortepiano.service.ContactoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/contactos")
@RequiredArgsConstructor
@Tag(name = "Contacto", description = "Endpoints para recepción y consulta de mensajes de contacto de clientes")
public class ContactoController {

    private final ContactoService contactoService;

    @GetMapping
    @Operation(summary = "Listar todos los mensajes de contacto", description = "Retorna los mensajes de contacto recibidos en la plataforma")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de mensajes obtenido exitosamente")
    })
    public ResponseEntity<List<MensajeContacto>> listarContactos() {
        return ResponseEntity.ok(contactoService.listarTodos());
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo mensaje de contacto", description = "Permite a los usuarios enviar un mensaje o consulta validando nombre, correo con dominio válido y comentario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mensaje de contacto registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o dominio de correo no autorizado")
    })
    public ResponseEntity<?> registrarContacto(@Valid @RequestBody MensajeContactoRequest request) {
        try {
            MensajeContacto guardado = contactoService.registrarContacto(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiErrorDto.builder()
                            .timestamp(LocalDateTime.now())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .error("Bad Request")
                            .message(e.getMessage())
                            .path("/api/v1/contactos")
                            .build()
            );
        }
    }
}
