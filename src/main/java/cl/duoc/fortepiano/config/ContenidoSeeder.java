package cl.duoc.fortepiano.config;

import cl.duoc.fortepiano.entity.Noticia;
import cl.duoc.fortepiano.repository.NoticiaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Datos iniciales del Módulo C (Contenido, Blog y Noticias).
 * Se ejecuta después de CatalogoBoletasSeeder (@Order(2)) con @Order(3).
 */
@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class ContenidoSeeder implements CommandLineRunner {

    private final NoticiaRepository noticiaRepository;

    @Override
    public void run(String... args) {
        log.info("Iniciando sembrado de datos: Módulo C (Contenido del Blog)...");
        sembrarNoticias();
    }

    private void sembrarNoticias() {
        if (noticiaRepository.count() > 0) {
            log.info("ContenidoSeeder: Las noticias del blog ya existen, no se insertan nuevamente.");
            repararImagenesExistentes();
            return;
        }

        List<Noticia> noticias = List.of(
                Noticia.builder()
                        .titulo("Cómo elegir tu primer piano digital: Guía completa 2026")
                        .resumen("Descubre los aspectos clave para elegir un piano digital: acción del teclado, polifonía, conectividad y relación precio-calidad.")
                        .imagen("blog/piano.webp")
                        .fecha("15-10-2026")
                        .slug("detalle-1")
                        .contenido(List.of(
                                "Elegir tu primer piano digital es una de las decisiones más importantes en el inicio de tu viaje musical. A diferencia de un teclado convencional, un piano digital busca recrear con la máxima fidelidad posible el tacto, la respuesta y el sonido acústico de un piano de cola o vertical.",
                                "El aspecto más crítico al elegir es el mecanismo de las teclas. Te recomendamos buscar teclados con teclas contrapesadas (mecanismo de martillo o hammer action), que ofrecen la resistencia gradual típica de los instrumentos acústicos, facilitando el desarrollo de la técnica correcta desde el primer día.",
                                "Otro factor determinante es la polifonía máxima, idealmente de 64 o 128 voces, para evitar que las notas se corten al usar el pedal de sustain en pasajes complejos. En Forte & Piano contamos con asesoría especializada para ayudarte a encontrar el instrumento ideal acorde a tu presupuesto y nivel."
                        ))
                        .build(),

                Noticia.builder()
                        .titulo("Mantenimiento y cuidado esencial para pianos acústicos")
                        .resumen("Aprende cómo proteger tu piano acústico de las variaciones de humedad, temperatura y la importancia de la afinación periódica.")
                        .imagen("blog/apertura_nueva.jpg")
                        .fecha("20-10-2026")
                        .slug("detalle-2")
                        .contenido(List.of(
                                "Un piano acústico es una obra maestra de la ingeniería compuesta por miles de piezas de madera, metal y fieltro. Mantenerlo en óptimas condiciones requiere atención cuidadosa a su entorno ambiental y cuidados periódicos.",
                                "La humedad relativa ideal para un piano acústico debe mantenerse entre el 40% y el 60%. Cambios drásticos de humedad pueden expandir o contraer la tabla armónica, desafinando el instrumento o provocando grietas irreversibles en la estructura.",
                                "Recomendamos realizar afinaciones profesionales al menos una o dos veces por año, además de evitar ubicar el piano cerca de fuentes de calor directo, ventanas expuestas a la luz solar directa o corrientes de aire continuas."
                        ))
                        .build(),

                Noticia.builder()
                        .titulo("Sintetizadores modernos: La evolución del sonido analógico y digital")
                        .resumen("Exploramos las diferencias entre síntesis analógica, digital y virtual analógica en los teclados de escenario actuales.")
                        .imagen("blog/apertura.jpg")
                        .fecha("25-10-2026")
                        .slug("detalle-3")
                        .contenido(List.of(
                                "El mundo de los sintetizadores ha experimentado una verdadera revolución en la última década. La coexistencia de tecnologías analógicas cálidas y motores digitales de alta precisión abre un abanico sonoro sin precedentes para productores y tecladistas.",
                                "Instrumentos icónicos como el Nord Electro o las series modernas de Roland y Yamaha combinan simulaciones de modelado físico con muestras de altísima resolución, permitiendo alternar entre órganos de ruedas tonales, pianos acústicos y sintetizadores FM con total versatilidad en el escenario.",
                                "En nuestra tienda física y online puedes probar y comparar los principales modelos del mercado, con demostraciones interactivas guiadas por músicos profesionales para encontrar tu propio sonido característico."
                        ))
                        .build()
        );

        noticiaRepository.saveAll(noticias);
        log.info("ContenidoSeeder: {} noticias del blog sembradas exitosamente.", noticias.size());
    }

    /**
     * Corrige rutas de imagen antiguas (prefijo "noticias/") que no existen en
     * public/img del frontend. Solo actúa sobre rutas vacías o rotas para no
     * sobrescribir valores válidos ya guardados.
     */
    private void repararImagenesExistentes() {
        Map<String, String> imagenesPorSlug = Map.of(
                "detalle-1", "blog/piano.webp",
                "detalle-2", "blog/apertura_nueva.jpg",
                "detalle-3", "blog/apertura.jpg"
        );

        List<Noticia> noticias = noticiaRepository.findAll();
        boolean cambios = false;

        for (Noticia noticia : noticias) {
            String imagenEsperada = imagenesPorSlug.get(noticia.getSlug());
            boolean rutaRota = noticia.getImagen() == null
                    || noticia.getImagen().isBlank()
                    || noticia.getImagen().startsWith("noticias/");

            if (imagenEsperada != null && rutaRota) {
                noticia.setImagen(imagenEsperada);
                cambios = true;
            }
        }

        if (cambios) {
            noticiaRepository.saveAll(noticias);
            log.info("ContenidoSeeder: rutas de imagen del blog reparadas.");
        }
    }
}
