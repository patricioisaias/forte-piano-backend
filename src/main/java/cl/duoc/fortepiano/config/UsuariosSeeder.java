package cl.duoc.fortepiano.config;

import cl.duoc.fortepiano.entity.Comuna;
import cl.duoc.fortepiano.entity.Region;
import cl.duoc.fortepiano.entity.Rol;
import cl.duoc.fortepiano.entity.Usuario;
import cl.duoc.fortepiano.repository.ComunaRepository;
import cl.duoc.fortepiano.repository.RegionRepository;
import cl.duoc.fortepiano.repository.RolRepository;
import cl.duoc.fortepiano.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class UsuariosSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Iniciando sembrado de datos: Módulo A (Usuarios, Roles y Geografía)...");

        // 1. Roles (1: Administrador, 2: Vendedor, 3: Cliente)
        if (rolRepository.count() == 0) {
            rolRepository.saveAll(List.of(
                    Rol.builder().id(1).nombre("Administrador").build(),
                    Rol.builder().id(2).nombre("Vendedor").build(),
                    Rol.builder().id(3).nombre("Cliente").build()
            ));
            log.info("3 Roles sembrados exitosamente.");
        }

        // 2. Regiones
        if (regionRepository.count() == 0) {
            regionRepository.saveAll(List.of(
                    Region.builder().id(13).nombre("Región Metropolitana de Santiago").build(),
                    Region.builder().id(5).nombre("Región de Valparaíso").build(),
                    Region.builder().id(8).nombre("Región del Biobío").build()
            ));
            log.info("3 Regiones sembradas exitosamente.");
        }

        // 3. Comunas
        if (comunaRepository.count() == 0) {
            comunaRepository.saveAll(List.of(
                    Comuna.builder().id(1).regionId(13).nombre("Santiago").build(),
                    Comuna.builder().id(2).regionId(13).nombre("Providencia").build(),
                    Comuna.builder().id(3).regionId(13).nombre("Las Condes").build(),
                    Comuna.builder().id(4).regionId(13).nombre("Maipú").build(),
                    Comuna.builder().id(5).regionId(5).nombre("Viña del Mar").build(),
                    Comuna.builder().id(6).regionId(5).nombre("Valparaíso").build(),
                    Comuna.builder().id(7).regionId(8).nombre("Concepción").build()
            ));
            log.info("7 Comunas sembradas exitosamente.");
        }

        // 4. Usuarios Semilla
        if (usuarioRepository.count() == 0) {
            usuarioRepository.saveAll(List.of(
                    // Administrador
                    Usuario.builder()
                            .run("11111111-1")
                            .nombre("Patricio")
                            .apellidos("Céspedes")
                            .correo("admin@gmail.com")
                            .password(passwordEncoder.encode("admin123"))
                            .fechaNacimiento("1990-05-15")
                            .rolId(1)
                            .regionId(13)
                            .comunaId(1)
                            .direccion("Av. Libertador Bernardo O'Higgins 1234")
                            .estado("Activo")
                            .build(),

                    // Vendedor
                    Usuario.builder()
                            .run("22222222-2")
                            .nombre("Daniel")
                            .apellidos("Aedo")
                            .correo("vendedor@gmail.com")
                            .password(passwordEncoder.encode("vend1234"))
                            .fechaNacimiento("1992-08-20")
                            .rolId(2)
                            .regionId(13)
                            .comunaId(2)
                            .direccion("Av. Providencia 1050")
                            .estado("Activo")
                            .build(),

                    // Cliente 1
                    Usuario.builder()
                            .run("12345678-5")
                            .nombre("Camila")
                            .apellidos("Silva")
                            .correo("camila@gmail.com")
                            .password(passwordEncoder.encode("cliente1"))
                            .fechaNacimiento("1998-11-03")
                            .rolId(3)
                            .regionId(5)
                            .comunaId(5)
                            .direccion("Av. Libertad 450")
                            .estado("Activo")
                            .build(),

                    // Cliente 2 (Duoc)
                    Usuario.builder()
                            .run("18765432-7")
                            .nombre("Diego")
                            .apellidos("Morales")
                            .correo("diego@duoc.cl")
                            .password(passwordEncoder.encode("diego123"))
                            .fechaNacimiento("2001-02-14")
                            .rolId(3)
                            .regionId(13)
                            .comunaId(4)
                            .direccion("Av. Pajaritos 2500")
                            .estado("Activo")
                            .build()
            ));
            log.info("4 Usuarios semilla sembrados exitosamente (con BCrypt).");
        }
    }
}
