package cl.duoc.fortepiano.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReintentoPagoRequest {

    @NotBlank(message = "El resultado de la transaccion es obligatorio")
    @Size(max = 30)
    private String resultadoTransaccion;

    @Size(max = 100)
    private String metodoPago;
}
