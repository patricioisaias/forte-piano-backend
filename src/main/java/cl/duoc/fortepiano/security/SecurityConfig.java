package cl.duoc.fortepiano.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Swagger / OpenAPI 3
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 2. Auth públicas (login, registro)
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/registro").permitAll()
                        .requestMatchers("/api/v1/auth/me", "/api/v1/auth/perfil").authenticated()

                        // 3. Catálogo, Ubicación, Noticias y Contacto públicos para lectura
                        .requestMatchers(HttpMethod.GET, "/api/v1/roles/**", "/api/v1/regiones/**", "/api/v1/comunas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**", "/api/v1/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/noticias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/contactos").permitAll()

                        // 4. Operaciones de Checkout y Mis Boletas (Cliente o Admin)
                        .requestMatchers(HttpMethod.POST, "/api/v1/boletas/checkout").hasAnyRole("CLIENTE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/boletas/mis-boletas").hasAnyRole("CLIENTE", "ADMIN")

                        // 5. Gestión de Usuarios (Exclusivo Administrador)
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMIN")

                        // 6. Mutaciones de Catálogo (Exclusivo Administrador)
                        .requestMatchers(HttpMethod.POST, "/api/v1/productos/**", "/api/v1/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/productos/**", "/api/v1/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**", "/api/v1/categorias/**").hasRole("ADMIN")

                        // 7. Anulación de boletas (Exclusivo Administrador)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/boletas/*/anular").hasRole("ADMIN")

                        // 8. Consulta de órdenes y Dashboard (Administrador y Vendedor)
                        .requestMatchers("/api/v1/boletas/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers("/api/v1/dashboard/**").hasAnyRole("ADMIN", "VENDEDOR")

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
