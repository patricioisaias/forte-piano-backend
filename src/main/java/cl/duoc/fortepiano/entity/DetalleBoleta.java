package cl.duoc.fortepiano.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detalle_boleta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleBoleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "boleta_id", nullable = false)
    private Boleta boleta;

    @NotBlank(message = "El codigo del producto es obligatorio")
    @Size(max = 20)
    @Column(name = "producto_codigo", nullable = false, length = 20)
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull(message = "El precio unitario es obligatorio")
    @Min(0)
    @Column(nullable = false)
    private Integer precioUnitario;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(1)
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "El subtotal es obligatorio")
    @Min(0)
    @Column(nullable = false)
    private Integer subtotal;
}
