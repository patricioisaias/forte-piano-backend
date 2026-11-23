package cl.duoc.fortepiano.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "noticia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Noticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El título de la noticia es obligatorio")
    @Size(max = 200, message = "El título no debe superar los 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    @NotBlank(message = "El resumen de la noticia es obligatorio")
    @Size(max = 500, message = "El resumen no debe superar los 500 caracteres")
    @Column(nullable = false, length = 500)
    private String resumen;

    @Size(max = 255, message = "La ruta de la imagen no debe superar los 255 caracteres")
    @Column(length = 255)
    private String imagen;

    @NotBlank(message = "La fecha es obligatoria")
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String fecha;

    @NotBlank(message = "El slug de la noticia es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "noticia_contenido", joinColumns = @JoinColumn(name = "noticia_id"))
    @Column(name = "parrafo", length = 3000)
    @OrderColumn(name = "orden")
    @Builder.Default
    private List<String> contenido = new ArrayList<>();
}
