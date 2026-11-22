package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.entity.Producto;
import cl.duoc.fortepiano.repository.CategoriaRepository;
import cl.duoc.fortepiano.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Producto> listar(String texto, Integer categoriaId, Boolean enOferta) {
        String filtro = texto == null ? "" : texto.toLowerCase(Locale.ROOT);
        return productoRepository.findAll().stream()
                .filter(p -> filtro.isEmpty() || p.getNombre().toLowerCase(Locale.ROOT).contains(filtro))
                .filter(p -> categoriaId == null || categoriaId.equals(p.getCategoriaId()))
                .filter(p -> enOferta == null || enOferta.equals(p.getEnOferta()))
                .collect(Collectors.toList());
    }

    public Producto obtener(String codigo) {
        return productoRepository.findById(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @Transactional
    public Producto crear(Producto producto) {
        if (productoRepository.existsById(producto.getCodigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con ese codigo");
        }
        validarCategoria(producto);
        aplicarValoresPorDefecto(producto);
        calcularPrecioOferta(producto);
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(String codigo, Producto datos) {
        Producto existente = obtener(codigo);
        validarCategoria(datos);

        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        existente.setStockCritico(datos.getStockCritico());
        existente.setCategoriaId(datos.getCategoriaId());
        existente.setImagen(datos.getImagen());
        existente.setEstado(datos.getEstado());
        existente.setEnOferta(datos.getEnOferta());
        existente.setPorcentajeDescuento(datos.getPorcentajeDescuento());
        existente.setPrecioOferta(datos.getPrecioOferta());

        aplicarValoresPorDefecto(existente);
        calcularPrecioOferta(existente);
        return productoRepository.save(existente);
    }

    @Transactional
    public void eliminar(String codigo) {
        if (!productoRepository.existsById(codigo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }
        productoRepository.deleteById(codigo);
    }

    private void validarCategoria(Producto producto) {
        if (producto.getCategoriaId() != null && !categoriaRepository.existsById(producto.getCategoriaId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoria indicada no existe");
        }
    }

    private void aplicarValoresPorDefecto(Producto producto) {
        if (producto.getEstado() == null || producto.getEstado().isBlank()) {
            producto.setEstado("Activo");
        }
        if (producto.getEnOferta() == null) {
            producto.setEnOferta(false);
        }
        if (producto.getPorcentajeDescuento() == null) {
            producto.setPorcentajeDescuento(0);
        }
        if (producto.getStockCritico() == null) {
            producto.setStockCritico(0);
        }
    }

    /** Misma formula que el frontend: precioOferta = round(precio * (1 - descuento/100)). */
    void calcularPrecioOferta(Producto producto) {
        if (Boolean.TRUE.equals(producto.getEnOferta())
                && producto.getPorcentajeDescuento() != null
                && producto.getPorcentajeDescuento() > 0
                && (producto.getPrecioOferta() == null || producto.getPrecioOferta() == 0)) {
            producto.setPrecioOferta(Math.round(producto.getPrecio() * (1 - producto.getPorcentajeDescuento() / 100.0f)));
        }
    }
}
