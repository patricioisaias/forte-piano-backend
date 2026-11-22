package cl.duoc.fortepiano.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @NotBlank(message = "El codigo del producto es obligatorio")
    @Size(min = 3, max = 20, message = "El codigo debe tener entre 3 y 20 caracteres")
    @Column(nullable = false, length = 20)
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no debe superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 500, message = "La descripcion no debe superar los 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private Integer precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @Min(value = 0, message = "El stock critico no puede ser negativo")
    private Integer stockCritico;

    /** Id de categoria que expone el JSON (compatible con el frontend React). */
    @Column(name = "categoria_id")
    private Integer categoriaId;

    /** Relacion real con la tabla categoria (Foreign Key en MySQL). */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", insertable = false, updatable = false)
    private Categoria categoria;

    @Size(max = 300, message = "La imagen no debe superar los 300 caracteres")
    private String imagen;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String estado;

    private Boolean enOferta;

    @Min(value = 0, message = "El descuento no puede ser negativo")
    @Max(value = 100, message = "El descuento no puede superar el 100%")
    private Integer porcentajeDescuento;

    @Min(value = 0, message = "El precio de oferta no puede ser negativo")
    private Integer precioOferta;
}
