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
@Table(name = "region")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
}
