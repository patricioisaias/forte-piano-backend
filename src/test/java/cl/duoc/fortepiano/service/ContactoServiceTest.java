package cl.duoc.fortepiano.service;

import cl.duoc.fortepiano.dto.MensajeContactoRequest;
import cl.duoc.fortepiano.entity.MensajeContacto;
import cl.duoc.fortepiano.repository.MensajeContactoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactoServiceTest {

    @Mock
    private MensajeContactoRepository mensajeContactoRepository;

    @InjectMocks
    private ContactoService contactoService;

    private MensajeContactoRequest requestValido;

    @BeforeEach
    void setUp() {
        requestValido = MensajeContactoRequest.builder()
                .nombre("Angelo Ponce")
                .correo("angelo@duoc.cl")
                .comentario("Consulta sobre despacho de accesorios.")
                .build();
    }

    @Test
    @DisplayName("registrarContacto con datos válidos registra y devuelve mensaje")
    void registrarContactoValido() {
        when(mensajeContactoRepository.save(any(MensajeContacto.class))).thenAnswer(i -> {
            MensajeContacto mc = i.getArgument(0);
            mc.setId(1L);
            return mc;
        });

        MensajeContacto creado = contactoService.registrarContacto(requestValido);

        assertNotNull(creado);
        assertEquals(1L, creado.getId());
        assertEquals("Angelo Ponce", creado.getNombre());
        assertEquals("angelo@duoc.cl", creado.getCorreo());
        assertNotNull(creado.getFecha());
        verify(mensajeContactoRepository, times(1)).save(any(MensajeContacto.class));
    }

    @Test
    @DisplayName("registrarContacto con dominio no autorizado lanza excepción")
    void registrarContactoDominioNoAutorizado() {
        MensajeContactoRequest invalido = MensajeContactoRequest.builder()
                .nombre("Usuario")
                .correo("usuario@yahoo.com")
                .comentario("Consulta")
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> contactoService.registrarContacto(invalido));

        assertTrue(ex.getMessage().contains("dominio válido"));
        verify(mensajeContactoRepository, never()).save(any(MensajeContacto.class));
    }

    @Test
    @DisplayName("registrarContacto con comentario mayor a 500 caracteres lanza excepción")
    void registrarContactoComentarioExcedeLimite() {
        MensajeContactoRequest largo = MensajeContactoRequest.builder()
                .nombre("Usuario")
                .correo("usuario@gmail.com")
                .comentario("A".repeat(501))
                .build();

        assertThrows(IllegalArgumentException.class, () -> contactoService.registrarContacto(largo));
    }

    @Test
    @DisplayName("listarTodos retorna lista ordenada de mensajes")
    void listarTodosRetornaLista() {
        MensajeContacto mc = MensajeContacto.builder()
                .id(1L)
                .nombre("Angelo")
                .correo("angelo@duoc.cl")
                .comentario("Comentario")
                .fecha("22-11-2026, 12:00:00")
                .build();

        when(mensajeContactoRepository.findAllByOrderByIdDesc()).thenReturn(List.of(mc));

        List<MensajeContacto> lista = contactoService.listarTodos();

        assertEquals(1, lista.size());
        verify(mensajeContactoRepository, times(1)).findAllByOrderByIdDesc();
    }

    @Test
    @DisplayName("registrarContacto acepta correos @gmail.com")
    void registrarContactoConGmailAceptado() {
        MensajeContactoRequest request = MensajeContactoRequest.builder()
                .nombre("Cliente Prueba")
                .correo("cliente.prueba@gmail.com")
                .comentario("¿Tienen stock del sintetizador Nord?")
                .build();

        when(mensajeContactoRepository.save(any(MensajeContacto.class)))
                .thenAnswer(i -> i.getArgument(0));

        MensajeContacto guardado = contactoService.registrarContacto(request);

        assertNotNull(guardado);
        assertEquals("cliente.prueba@gmail.com", guardado.getCorreo());
    }

    @Test
    @DisplayName("registrarContacto rechaza nombre o correo nulos/vacíos")
    void registrarContactoCamposObligatoriosLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> contactoService.registrarContacto(null));

        MensajeContactoRequest sinNombre = MensajeContactoRequest.builder()
                .nombre("  ")
                .correo("valido@duoc.cl")
                .comentario("Hola")
                .build();
        assertThrows(IllegalArgumentException.class,
                () -> contactoService.registrarContacto(sinNombre));

        MensajeContactoRequest sinCorreo = MensajeContactoRequest.builder()
                .nombre("Nombre")
                .correo("")
                .comentario("Hola")
                .build();
        assertThrows(IllegalArgumentException.class,
                () -> contactoService.registrarContacto(sinCorreo));
    }

    @Test
    @DisplayName("esDominioValido acepta y rechaza los dominios configurados")
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
