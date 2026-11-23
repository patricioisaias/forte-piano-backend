package cl.duoc.fortepiano.controller;

import cl.duoc.fortepiano.dto.ApiErrorDto;
import cl.duoc.fortepiano.entity.Noticia;
import cl.duoc.fortepiano.service.NoticiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/noticias")
@RequiredArgsConstructor
@Tag(name = "Noticias / Blog", description = "Endpoints para consultar noticias y artículos del blog de Forte & Piano")
public class NoticiaController {

    private final NoticiaService noticiaService;

    @GetMapping
    @Operation(summary = "Listar todas las noticias del blog", description = "Retorna el listado completo de artículos y noticias ordenadas para el blog")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de noticias obtenido exitosamente")
    })
    public ResponseEntity<List<Noticia>> listarNoticias() {
        return ResponseEntity.ok(noticiaService.listarTodas());
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Obtener noticia por su slug", description = "Retorna el contenido completo con párrafos de una noticia específica según su slug")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Noticia encontrada"),
            @ApiResponse(responseCode = "404", description = "Noticia no encontrada con el slug indicado")
    })
    public ResponseEntity<?> obtenerPorSlug(@PathVariable String slug) {
        return noticiaService.buscarPorSlug(slug)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiErrorDto.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("Not Found")
                                .message("No se encontró la noticia con el slug: " + slug)
                                .path("/api/v1/noticias/" + slug)
                                .build()
                ));
    }
}
