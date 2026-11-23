package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.entity.Noticia;
import cl.duoc.fortepiano.repository.NoticiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticiaService {

    private final NoticiaRepository noticiaRepository;

    @Transactional(readOnly = true)
    public List<Noticia> listarTodas() {
        return noticiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Noticia> buscarPorSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            return Optional.empty();
        }
        return noticiaRepository.findBySlug(slug);
    }

    @Transactional
    public Noticia guardar(Noticia noticia) {
        if (noticia == null) {
            throw new IllegalArgumentException("La noticia no puede ser nula");
        }
        if (noticia.getSlug() == null || noticia.getSlug().isBlank()) {
            throw new IllegalArgumentException("El slug de la noticia no puede estar vacío");
        }
        return noticiaRepository.save(noticia);
    }
}
