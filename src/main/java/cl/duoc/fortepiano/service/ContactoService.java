package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.dto.MensajeContactoRequest;
import cl.duoc.fortepiano.entity.MensajeContacto;
import cl.duoc.fortepiano.repository.MensajeContactoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ContactoService {

    private static final List<String> DOMINIOS_VALIDOS = List.of(
            "@duoc.cl",
            "@profesor.duoc.cl",
            "@gmail.com"
    );

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");

    private final MensajeContactoRepository mensajeContactoRepository;

    @Transactional(readOnly = true)
    public List<MensajeContacto> listarTodos() {
        return mensajeContactoRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public MensajeContacto registrarContacto(MensajeContactoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de contacto no puede ser nula");
        }

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (request.getCorreo() == null || request.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }

        if (!esDominioValido(request.getCorreo())) {
            throw new IllegalArgumentException("El correo debe pertenecer a un dominio válido (@gmail.com, @duoc.cl, @profesor.duoc.cl)");
        }

        if (request.getComentario() == null || request.getComentario().isBlank()) {
            throw new IllegalArgumentException("El comentario es obligatorio");
        }

        if (request.getComentario().trim().length() > 500) {
            throw new IllegalArgumentException("El comentario no debe superar los 500 caracteres");
        }

        String fecha = LocalDateTime.now().format(FORMATTER);

        MensajeContacto mensaje = MensajeContacto.builder()
                .nombre(request.getNombre().trim())
                .correo(request.getCorreo().trim().toLowerCase(Locale.ROOT))
                .comentario(request.getComentario().trim())
                .fecha(fecha)
                .build();

        return mensajeContactoRepository.save(mensaje);
    }

    public boolean esDominioValido(String correo) {
        if (correo == null || correo.isBlank()) {
            return false;
        }
        String normalizado = correo.trim().toLowerCase(Locale.ROOT);
        return DOMINIOS_VALIDOS.stream().anyMatch(normalizado::endsWith);
    }
}
