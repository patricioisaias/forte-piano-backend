package cl.duoc.fortepiano.repository;

import cl.duoc.fortepiano.entity.Comuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Integer> {
    List<Comuna> findByRegionId(Integer regionId);
}
