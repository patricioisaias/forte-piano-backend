package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.entity.Noticia;
import cl.duoc.fortepiano.repository.NoticiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticiaServiceTest {

    @Mock
    private NoticiaRepository noticiaRepository;

    @InjectMocks
    private NoticiaService noticiaService;

    private Noticia noticia;

    @BeforeEach
    void setUp() {
        noticia = Noticia.builder()
                .id(1)
                .titulo("Cómo elegir tu primer piano digital: Guía completa 2026")
                .resumen("Descubre los aspectos clave para elegir un piano digital.")
                .imagen("noticias/noticia-guia-piano-digital.webp")
                .fecha("15-10-2026")
                .slug("detalle-1")
                .contenido(List.of("Párrafo 1", "Párrafo 2"))
                .build();
    }

    @Test
    @DisplayName("listarTodas retorna la lista completa de noticias")
    void listarTodasRetornaLista() {
        when(noticiaRepository.findAll()).thenReturn(List.of(noticia));

        List<Noticia> lista = noticiaService.listarTodas();

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("detalle-1", lista.get(0).getSlug());
        verify(noticiaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorSlug retorna la noticia correspondiente")
    void buscarPorSlugEncontrado() {
        when(noticiaRepository.findBySlug("detalle-1")).thenReturn(Optional.of(noticia));

        Optional<Noticia> encontrada = noticiaService.buscarPorSlug("detalle-1");

        assertTrue(encontrada.isPresent());
        assertEquals("Cómo elegir tu primer piano digital: Guía completa 2026", encontrada.get().getTitulo());
        assertEquals(2, encontrada.get().getContenido().size());
    }

    @Test
    @DisplayName("buscarPorSlug retorna vacío para slug no encontrado")
    void buscarPorSlugNoEncontrado() {
        when(noticiaRepository.findBySlug("inexistente")).thenReturn(Optional.empty());

        Optional<Noticia> noEncontrada = noticiaService.buscarPorSlug("inexistente");

        assertTrue(noEncontrada.isEmpty());
        assertTrue(noticiaService.buscarPorSlug(null).isEmpty());
        assertTrue(noticiaService.buscarPorSlug("   ").isEmpty());
    }

    @Test
    @DisplayName("guardar persiste y retorna la noticia")
    void guardarNoticiaExitosa() {
        when(noticiaRepository.save(any(Noticia.class))).thenAnswer(i -> i.getArgument(0));

        Noticia guardada = noticiaService.guardar(noticia);

        assertNotNull(guardada);
        assertEquals("detalle-1", guardada.getSlug());
        verify(noticiaRepository, times(1)).save(noticia);
    }

    @Test
    @DisplayName("guardar lanza IllegalArgumentException si slug está vacío o noticia es nula")
    void guardarLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> noticiaService.guardar(null));

        Noticia invalida = Noticia.builder().titulo("Sin slug").slug("").build();
        assertThrows(IllegalArgumentException.class, () -> noticiaService.guardar(invalida));
    }
}
