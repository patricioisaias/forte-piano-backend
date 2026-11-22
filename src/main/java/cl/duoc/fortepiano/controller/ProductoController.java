package cl.duoc.fortepiano.controller;

import cl.duoc.fortepiano.entity.Producto;
import cl.duoc.fortepiano.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) Boolean enOferta) {
        return productoService.listar(texto, categoriaId, enOferta);
    }

    @GetMapping("/{codigo}")
    public Producto obtener(@PathVariable String codigo) {
        return productoService.obtener(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@Valid @RequestBody Producto producto) {
        return productoService.crear(producto);
    }

    @PutMapping("/{codigo}")
    public Producto actualizar(@PathVariable String codigo, @Valid @RequestBody Producto producto) {
        return productoService.actualizar(codigo, producto);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String codigo) {
        productoService.eliminar(codigo);
    }
}
