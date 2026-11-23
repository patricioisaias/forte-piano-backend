package cl.duoc.fortepiano.repository;

import cl.duoc.fortepiano.entity.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Integer> {
    Optional<Noticia> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
