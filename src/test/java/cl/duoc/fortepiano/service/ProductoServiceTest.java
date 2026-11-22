package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.entity.Producto;
import cl.duoc.fortepiano.repository.CategoriaRepository;
import cl.duoc.fortepiano.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void crearCalculaPrecioOfertaAutomaticamente() {
        Producto nuevo = producto("AD-001", "Funda GTSA Key 61 con Ruedas", 25000, 15, 4, true, 20, null);
        when(productoRepository.existsById("AD-001")).thenReturn(false);
        when(categoriaRepository.existsById(4)).thenReturn(true);
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto guardado = productoService.crear(nuevo);

        assertEquals(20000, guardado.getPrecioOferta());
        assertEquals("Activo", guardado.getEstado());
        assertEquals(0, guardado.getStockCritico());
    }

    @Test
    void crearNoCalculaPrecioOfertaSiElProductoNoEstaEnOferta() {
        Producto nuevo = producto("AC-005", "Soporte de Teclado Doble", 17990, 10, 4, false, 0, null);
        when(productoRepository.existsById("AC-005")).thenReturn(false);
        when(categoriaRepository.existsById(4)).thenReturn(true);
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto guardado = productoService.crear(nuevo);

        assertNull(guardado.getPrecioOferta());
    }

    @Test
    void crearLanzaConflictoSiElCodigoYaExiste() {
        Producto nuevo = producto("PD-001", "Yamaha P-45 Negro", 459990, 30, 2, true, 15, 390990);
        when(productoRepository.existsById("PD-001")).thenReturn(true);

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class, () -> productoService.crear(nuevo));

        assertEquals(409, excepcion.getStatusCode().value());
    }

    @Test
    void crearLanzaBadRequestSiLaCategoriaNoExiste() {
        Producto nuevo = producto("XX-001", "Producto sin categoria", 1000, 1, 99, false, 0, null);
        when(productoRepository.existsById("XX-001")).thenReturn(false);
        when(categoriaRepository.existsById(99)).thenReturn(false);

        ResponseStatusException excepcion = assertThrows(
                ResponseStatusException.class, () -> productoService.crear(nuevo));

        assertEquals(400, excepcion.getStatusCode().value());
    }

    @Test
    void listarFiltraPorTextoCategoriaYOferta() {
        Producto yamahaP45 = producto("PD-001", "Yamaha P-45 Negro", 459990, 30, 2, true, 15, 390990);
        Producto yamahaB1 = producto("PA-001", "Piano Yamaha B1", 3299000, 5, 1, false, 0, null);
        Producto nord = producto("SY-001", "Nord Electro 6D 61", 1899990, 12, 3, true, 10, 1709990);
        when(productoRepository.findAll()).thenReturn(List.of(yamahaP45, yamahaB1, nord));

        assertEquals(2, productoService.listar("yamaha", null, null).size());
        assertEquals(1, productoService.listar(null, 2, null).size());
        assertEquals(2, productoService.listar(null, null, true).size());
        assertEquals(1, productoService.listar("nord", null, true).size());
        assertEquals(0, productoService.listar("no-existe", null, null).size());
    }

    private Producto producto(String codigo, String nombre, int precio, int stock, int categoriaId,
                              boolean enOferta, int porcentajeDescuento, Integer precioOferta) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setDescripcion("Descripcion de prueba");
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setCategoriaId(categoriaId);
        producto.setImagen("productos/prueba.webp");
        producto.setEstado("Activo");
        producto.setEnOferta(enOferta);
        producto.setPorcentajeDescuento(porcentajeDescuento);
        producto.setPrecioOferta(precioOferta);
        return producto;
    }
}
