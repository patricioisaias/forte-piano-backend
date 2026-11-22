package cl.duoc.fortepiano.security;

import cl.duoc.fortepiano.entity.Usuario;
import cl.duoc.fortepiano.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

        String rolName = switch (usuario.getRolId() != null ? usuario.getRolId() : 3) {
            case 1 -> "ROLE_ADMIN";
            case 2 -> "ROLE_VENDEDOR";
            default -> "ROLE_CLIENTE";
        };

        boolean activo = "Activo".equalsIgnoreCase(usuario.getEstado());

        return new User(
                usuario.getCorreo(),
                usuario.getPassword(),
                activo,
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(rolName))
        );
    }
}
