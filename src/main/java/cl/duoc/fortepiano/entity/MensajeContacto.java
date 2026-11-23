package cl.duoc.fortepiano.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mensaje_contacto")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeContacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String correo;

    @NotBlank(message = "El comentario es obligatorio")
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String comentario;

    @NotBlank(message = "La fecha es obligatoria")
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String fecha;
}
