package cl.duoc.fortepiano.controller;

import cl.duoc.fortepiano.entity.Comuna;
import cl.duoc.fortepiano.entity.Region;
import cl.duoc.fortepiano.entity.Rol;
import cl.duoc.fortepiano.repository.ComunaRepository;
import cl.duoc.fortepiano.repository.RegionRepository;
import cl.duoc.fortepiano.repository.RolRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Ubicación y Roles", description = "Endpoints para consulta de roles, regiones y comunas")
public class UbicacionRolController {

    private final RolRepository rolRepository;
    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;

    @GetMapping("/roles")
    @Operation(summary = "Listar todos los roles del sistema")
    public ResponseEntity<List<Rol>> listarRoles() {
        return ResponseEntity.ok(rolRepository.findAll());
    }

    @GetMapping("/regiones")
    @Operation(summary = "Listar todas las regiones")
    public ResponseEntity<List<Region>> listarRegiones() {
        return ResponseEntity.ok(regionRepository.findAll());
    }

    @GetMapping("/comunas")
    @Operation(summary = "Listar comunas, con filtro opcional por regionId")
    public ResponseEntity<List<Comuna>> listarComunas(
            @RequestParam(name = "regionId", required = false) Integer regionId) {
        if (regionId != null) {
            return ResponseEntity.ok(comunaRepository.findByRegionId(regionId));
        }
        return ResponseEntity.ok(comunaRepository.findAll());
    }
}
