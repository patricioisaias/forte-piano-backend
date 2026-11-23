package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.dto.MensajeContactoRequest;
import cl.duoc.fortepiano.entity.MensajeContacto;
import cl.duoc.fortepiano.entity.Noticia;
import cl.duoc.fortepiano.repository.MensajeContactoRepository;
import cl.duoc.fortepiano.repository.NoticiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
class NoticiaContactoServiceTest {

    @Nested
    @DisplayName("Pruebas Unitarias para NoticiaService")
    class NoticiaServiceTests {

        @Mock
        private NoticiaRepository noticiaRepository;

        @InjectMocks
        private NoticiaService noticiaService;

        private Noticia noticiaEjemplo;

        @BeforeEach
        void setUp() {
            noticiaEjemplo = Noticia.builder()
                    .id(1)
                    .titulo("Cómo elegir tu primer piano digital: Guía completa 2026")
                    .resumen("Descubre los aspectos clave para elegir un piano digital.")
                    .imagen("noticias/noticia-guia-piano-digital.webp")
                    .fecha("15-10-2026")
                    .slug("detalle-1")
                    .contenido(List.of("Párrafo 1 de prueba", "Párrafo 2 de prueba"))
                    .build();
        }

        @Test
        @DisplayName("listarTodas debe retornar la lista de todas las noticias")
        void listarTodasRetornaLista() {
            when(noticiaRepository.findAll()).thenReturn(List.of(noticiaEjemplo));

            List<Noticia> resultado = noticiaService.listarTodas();

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals("detalle-1", resultado.get(0).getSlug());
            verify(noticiaRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("buscarPorSlug debe retornar la noticia cuando el slug existe")
        void buscarPorSlugEncontrado() {
            when(noticiaRepository.findBySlug("detalle-1")).thenReturn(Optional.of(noticiaEjemplo));

            Optional<Noticia> resultado = noticiaService.buscarPorSlug("detalle-1");

            assertTrue(resultado.isPresent());
            assertEquals("Cómo elegir tu primer piano digital: Guía completa 2026", resultado.get().getTitulo());
            assertEquals(2, resultado.get().getContenido().size());
            verify(noticiaRepository, times(1)).findBySlug("detalle-1");
        }

        @Test
        @DisplayName("buscarPorSlug debe retornar vacío cuando el slug no existe o es nulo/blanco")
        void buscarPorSlugNoEncontrado() {
            when(noticiaRepository.findBySlug("slug-inexistente")).thenReturn(Optional.empty());

            Optional<Noticia> resultado = noticiaService.buscarPorSlug("slug-inexistente");
            assertTrue(resultado.isEmpty());

            Optional<Noticia> resultadoNulo = noticiaService.buscarPorSlug(null);
            assertTrue(resultadoNulo.isEmpty());

            Optional<Noticia> resultadoBlanco = noticiaService.buscarPorSlug("   ");
            assertTrue(resultadoBlanco.isEmpty());
        }

        @Test
        @DisplayName("guardar debe persistir y retornar la noticia válida")
        void guardarNoticiaExitosa() {
            when(noticiaRepository.save(any(Noticia.class))).thenAnswer(inv -> inv.getArgument(0));

            Noticia guardada = noticiaService.guardar(noticiaEjemplo);

            assertNotNull(guardada);
            assertEquals("detalle-1", guardada.getSlug());
            verify(noticiaRepository, times(1)).save(noticiaEjemplo);
        }

        @Test
        @DisplayName("guardar debe lanzar excepción si la noticia es nula o no tiene slug")
        void guardarNoticiaInvalidaLanzaExcepcion() {
            assertThrows(IllegalArgumentException.class, () -> noticiaService.guardar(null));

            Noticia sinSlug = Noticia.builder().titulo("Sin slug").slug("").build();
            assertThrows(IllegalArgumentException.class, () -> noticiaService.guardar(sinSlug));
        }
    }

    @Nested
    @DisplayName("Pruebas Unitarias para ContactoService")
    class ContactoServiceTests {

        @Mock
        private MensajeContactoRepository mensajeContactoRepository;

        @InjectMocks
        private ContactoService contactoService;

        private MensajeContactoRequest requestValido;

        @BeforeEach
        void setUp() {
            requestValido = MensajeContactoRequest.builder()
                    .nombre("Angelo Ponce")
                    .correo("an.poncev@duocuc.cl") // Dominio institucional duoc
                    .comentario("Consulta sobre disponibilidad de piano acústico Yamaha B1.")
                    .build();
        }

        @Test
        @DisplayName("registrarContacto con datos válidos y dominio @duoc.cl debe guardar exitosamente")
        void registrarContactoExitoso() {
            MensajeContactoRequest req = MensajeContactoRequest.builder()
                    .nombre("Angelo Ponce")
                    .correo("angelo@duoc.cl")
                    .comentario("Consulta sobre despacho de teclados.")
                    .build();

            when(mensajeContactoRepository.save(any(MensajeContacto.class))).thenAnswer(inv -> {
                MensajeContacto mc = inv.getArgument(0);
                mc.setId(10L);
                return mc;
            });

            MensajeContacto guardado = contactoService.registrarContacto(req);

            assertNotNull(guardado);
            assertEquals(10L, guardado.getId());
            assertEquals("Angelo Ponce", guardado.getNombre());
            assertEquals("angelo@duoc.cl", guardado.getCorreo());
            assertNotNull(guardado.getFecha());
            verify(mensajeContactoRepository, times(1)).save(any(MensajeContacto.class));
        }

        @Test
        @DisplayName("registrarContacto con correo @gmail.com debe ser aceptado")
        void registrarContactoConGmailAceptado() {
            MensajeContactoRequest req = MensajeContactoRequest.builder()
                    .nombre("Cliente Prueba")
                    .correo("cliente.prueba@gmail.com")
                    .comentario("¿Tienen stock del sintetizador Nord?")
                    .build();

            when(mensajeContactoRepository.save(any(MensajeContacto.class))).thenAnswer(inv -> inv.getArgument(0));

            MensajeContacto guardado = contactoService.registrarContacto(req);

            assertNotNull(guardado);
            assertEquals("cliente.prueba@gmail.com", guardado.getCorreo());
        }

        @Test
        @DisplayName("registrarContacto con correo de dominio no autorizado debe lanzar IllegalArgumentException")
        void registrarContactoDominioInvalidoLanzaExcepcion() {
            MensajeContactoRequest req = MensajeContactoRequest.builder()
                    .nombre("Usuario Externo")
                    .correo("usuario@hotmail.com")
                    .comentario("Comentario de prueba")
                    .build();

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> contactoService.registrarContacto(req));

            assertTrue(ex.getMessage().contains("dominio válido"));
            verify(mensajeContactoRepository, never()).save(any(MensajeContacto.class));
        }

        @Test
        @DisplayName("registrarContacto con comentario mayor a 500 caracteres debe lanzar excepción")
        void registrarContactoComentarioDemasiadoLargoLanzaExcepcion() {
            String comentarioLargo = "A".repeat(501);
            MensajeContactoRequest req = MensajeContactoRequest.builder()
                    .nombre("Usuario")
                    .correo("usuario@gmail.com")
                    .comentario(comentarioLargo)
                    .build();

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> contactoService.registrarContacto(req));

            assertTrue(ex.getMessage().contains("500 caracteres"));
            verify(mensajeContactoRepository, never()).save(any(MensajeContacto.class));
        }

        @Test
        @DisplayName("registrarContacto con nombre o correo nulos/vacíos debe lanzar excepción")
        void registrarContactoCamposObligatoriosLanzaExcepcion() {
            assertThrows(IllegalArgumentException.class, () -> contactoService.registrarContacto(null));

            MensajeContactoRequest sinNombre = MensajeContactoRequest.builder()
                    .nombre("  ")
                    .correo("valido@duoc.cl")
                    .comentario("Hola")
                    .build();
            assertThrows(IllegalArgumentException.class, () -> contactoService.registrarContacto(sinNombre));

            MensajeContactoRequest sinCorreo = MensajeContactoRequest.builder()
                    .nombre("Nombre")
                    .correo("")
                    .comentario("Hola")
                    .build();
            assertThrows(IllegalArgumentException.class, () -> contactoService.registrarContacto(sinCorreo));
        }

        @Test
        @DisplayName("listarTodos debe retornar lista de contactos ordenada")
        void listarTodosRetornaLista() {
            MensajeContacto mc = MensajeContacto.builder()
                    .id(1L)
                    .nombre("Angelo")
                    .correo("angelo@duoc.cl")
                    .comentario("Prueba")
                    .fecha("22-11-2026, 21:00:00")
                    .build();

            when(mensajeContactoRepository.findAllByOrderByIdDesc()).thenReturn(List.of(mc));

            List<MensajeContacto> lista = contactoService.listarTodos();

            assertEquals(1, lista.size());
            assertEquals("Angelo", lista.get(0).getNombre());
            verify(mensajeContactoRepository, times(1)).findAllByOrderByIdDesc();
        }

        @Test
        @DisplayName("esDominioValido valida correctamente los dominios permitidos")
        void esDominioValidoCasos() {
            assertTrue(contactoService.esDominioValido("test@gmail.com"));
            assertTrue(contactoService.esDominioValido("docente@profesor.duoc.cl"));
            assertTrue(contactoService.esDominioValido("alumno@duoc.cl"));
            assertFalse(contactoService.esDominioValido("test@yahoo.es"));
            assertFalse(contactoService.esDominioValido("test@outlook.com"));
            assertFalse(contactoService.esDominioValido(null));
            assertFalse(contactoService.esDominioValido(""));
        }
    }
}
