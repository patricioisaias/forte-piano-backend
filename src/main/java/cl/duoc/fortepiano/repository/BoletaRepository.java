package cl.duoc.fortepiano.repository;

import cl.duoc.fortepiano.entity.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoletaRepository extends JpaRepository<Boleta, String> {

    List<Boleta> findByClienteRunIgnoreCase(String clienteRun);

    List<Boleta> findByClienteCorreoIgnoreCase(String clienteCorreo);

    Optional<Boleta> findTopByOrderByFolioDesc();
}
