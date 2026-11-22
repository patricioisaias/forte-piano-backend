package cl.duoc.fortepiano.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boleta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Boleta {

    @Id
    @NotBlank(message = "El id de la boleta es obligatorio")
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String id;

    @NotNull(message = "El folio es obligatorio")
    @Column(nullable = false, unique = true)
    private Integer folio;

    @NotBlank(message = "La fecha es obligatoria")
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String fecha;

    /** RUN del comprador. Se guarda en usuario_run para no depender de Usuario.java. */
    @Size(max = 20)
    @Column(name = "usuario_run", length = 20)
    private String clienteRun;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String clienteNombre;

    @NotBlank(message = "El correo del cliente es obligatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String clienteCorreo;

    @NotNull(message = "El subtotal neto es obligatorio")
    @Min(0)
    @Column(nullable = false)
    private Integer subtotalNeto;

    @NotNull(message = "El IVA es obligatorio")
    @Min(0)
    @Column(nullable = false)
    private Integer iva;

    @NotNull(message = "El total es obligatorio")
    @Min(0)
    @Column(nullable = false)
    private Integer total;

    @Size(max = 100)
    @Column(length = 100)
    private String metodoPago;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String estado;

    @Size(max = 255)
    @Column(length = 255)
    private String mensajeTransaccion;

    // Datos de envio propios de la boleta (no se copian del perfil del usuario).
    @Size(max = 30)
    @Column(length = 30)
    private String modalidad;

    @Size(max = 150)
    @Column(length = 150)
    private String destinatario;

    @Size(max = 30)
    @Column(length = 30)
    private String telefono;

    private Integer regionId;

    @Size(max = 100)
    @Column(length = 100)
    private String regionNombre;

    private Integer comunaId;

    @Size(max = 100)
    @Column(length = 100)
    private String comunaNombre;

    @Size(max = 255)
    @Column(length = 255)
    private String direccion;

    @Size(max = 255)
    @Column(length = 255)
    private String referencia;

    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleBoleta> items = new ArrayList<>();
}
