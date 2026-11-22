package cl.duoc.fortepiano.repository;

import cl.duoc.fortepiano.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, String> {

    List<Producto> findByCategoriaId(Integer categoriaId);

    List<Producto> findByNombreContainingIgnoreCase(String texto);
}
