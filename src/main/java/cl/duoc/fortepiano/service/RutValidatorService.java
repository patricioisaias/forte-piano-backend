package cl.duoc.fortepiano.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class RutValidatorService {

    private static final List<String> DOMINIOS_PERMITIDOS = List.of(
            "@duoc.cl",
            "@profesor.duoc.cl",
            "@gmail.com"
    );

    /**
     * Limpia el RUN eliminando puntos, espacios y guiones, retornando en mayúsculas.
     */
    public String limpiarRun(String run) {
        if (run == null) {
            return "";
        }
        return run.replaceAll("[^0-9kK]", "").toUpperCase(Locale.ROOT);
    }

    /**
     * Calcula el dígito verificador para un cuerpo numérico usando Módulo 11.
     */
    public char calcularDigitoVerificador(String cuerpo) {
        int suma = 0;
        int multiplicador = 2;

        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(cuerpo.charAt(i));
            suma += digito * multiplicador;
            multiplicador = (multiplicador == 7) ? 2 : multiplicador + 1;
        }

        int resto = suma % 11;
        int valor = 11 - resto;

        if (valor == 11) {
            return '0';
        } else if (valor == 10) {
            return 'K';
        } else {
            return Character.forDigit(valor, 10);
        }
    }

    /**
     * Valida si un RUN cumple estrictamente con el algoritmo Módulo 11
     * y posee al menos cuerpo y dígito verificador.
     */
    public boolean validarRun(String run) {
        if (run == null) {
            return false;
        }

        String limpio = limpiarRun(run);
        if (limpio.length() < 8 || limpio.length() > 9) {
            return false;
        }

        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dvIngresado = limpio.charAt(limpio.length() - 1);

        if (!cuerpo.matches("^\\d+$")) {
            return false;
        }

        char dvCalculado = calcularDigitoVerificador(cuerpo);
        return dvIngresado == dvCalculado;
    }

    /**
     * Valida que el correo termine en uno de los dominios institucionales o autorizados.
     */
    public boolean validarDominioCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return false;
        }
        String normalizado = correo.trim().toLowerCase(Locale.ROOT);
        return DOMINIOS_PERMITIDOS.stream().anyMatch(normalizado::endsWith);
    }

    /**
     * Formatea un RUN limpio al formato estándar: 12.345.678-K
     */
    public String formatearRun(String run) {
        String limpio = limpiarRun(run);
        if (limpio.length() < 2) {
            return limpio;
        }
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);

        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            sb.insert(0, cuerpo.charAt(i));
            count++;
            if (count % 3 == 0 && i > 0) {
                sb.insert(0, '.');
            }
        }
        return sb + "-" + dv;
    }
}
