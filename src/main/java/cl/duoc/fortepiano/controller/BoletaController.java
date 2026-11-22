package cl.duoc.fortepiano.controller;

import cl.duoc.fortepiano.dto.BoletaResponseDto;
import cl.duoc.fortepiano.dto.CheckoutRequest;
import cl.duoc.fortepiano.dto.ReintentoPagoRequest;
import cl.duoc.fortepiano.service.BoletaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boletas")
public class BoletaController {

    private final BoletaService boletaService;

    public BoletaController(BoletaService boletaService) {
        this.boletaService = boletaService;
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public BoletaResponseDto checkout(@Valid @RequestBody CheckoutRequest request) {
        return boletaService.checkout(request);
    }

    @GetMapping
    public List<BoletaResponseDto> listar(@RequestParam(required = false) String clienteRun) {
        return boletaService.listar(clienteRun);
    }

    @GetMapping("/mis-boletas")
    public List<BoletaResponseDto> misBoletas(
            @RequestParam(required = false) String clienteRun,
            @RequestParam(required = false) String correo) {
        return boletaService.misBoletas(clienteRun, correo);
    }

    @GetMapping("/{id}")
    public BoletaResponseDto obtener(@PathVariable String id) {
        return boletaService.obtener(id);
    }

    @PutMapping("/{id}/reintentar-pago")
    public BoletaResponseDto reintentarPago(
            @PathVariable String id,
            @Valid @RequestBody ReintentoPagoRequest request) {
        return boletaService.reintentarPago(id, request);
    }

    @PutMapping("/{id}/anular")
    public BoletaResponseDto anular(@PathVariable String id) {
        return boletaService.anular(id);
    }
}
