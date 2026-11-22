package cl.duoc.fortepiano.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rol")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @Column(name = "id")
    private Integer id; // 1 = Administrador, 2 = Vendedor, 3 = Cliente

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
}
