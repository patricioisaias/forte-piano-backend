package cl.duoc.fortepiano.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class RutValidatorServiceTest {

    private RutValidatorService rutValidatorService;

    @BeforeEach
    void setUp() {
        rutValidatorService = new RutValidatorService();
    }

    @Test
    @DisplayName("Debe limpiar RUN eliminando puntos, guiones y espacios")
    void testLimpiarRun() {
        assertEquals("123456785", rutValidatorService.limpiarRun("12.345.678-5"));
        assertEquals("111111111", rutValidatorService.limpiarRun(" 11.111.111-1 "));
        assertEquals("12345678K", rutValidatorService.limpiarRun("12345678-k"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "11111111-1",
            "22222222-2",
            "12345678-5",
            "18765432-7"
    })
    @DisplayName("Debe validar como válidos los RUNs de prueba que cumplen Módulo 11")
    void testValidarRunValidos(String run) {
        assertTrue(rutValidatorService.validarRun(run), "El RUN " + run + " debería ser válido");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "11111111-2", // DV incorrecto
            "12345678-9", // DV incorrecto
            "1234567",    // Sin DV o muy corto
            "abc",        // No numérico
            "",           // Vacío
            "123456789012"// Excesivamente largo
    })
    @DisplayName("Debe rechazar RUNs inválidos o con dígito verificador erróneo")
    void testValidarRunInvalidos(String run) {
        assertFalse(rutValidatorService.validarRun(run), "El RUN " + run + " debería ser rechazado");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "usuario@gmail.com",
            "alumno@duoc.cl",
            "docente@profesor.duoc.cl"
    })
    @DisplayName("Debe aceptar correos con dominios autorizados (@duoc.cl, @profesor.duoc.cl, @gmail.com)")
    void testValidarDominioCorreoValido(String correo) {
        assertTrue(rutValidatorService.validarDominioCorreo(correo));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "usuario@hotmail.com",
            "otro@yahoo.es",
            "correo@empresa.org",
            "",
            "sin-arroba"
    })
    @DisplayName("Debe rechazar correos con dominios no autorizados")
    void testValidarDominioCorreoInvalido(String correo) {
        assertFalse(rutValidatorService.validarDominioCorreo(correo));
    }
}
