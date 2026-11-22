package cl.duoc.fortepiano.security;

import cl.duoc.fortepiano.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret:FortePianoClaveSecretaSuperSeguraEvaluacion3DuocUC2026!}")
    private String secretKey;

    @Value("${jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extraerCorreo(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generarToken(Usuario usuario) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("run", usuario.getRun());
        extraClaims.put("nombre", usuario.getNombre());
        extraClaims.put("apellidos", usuario.getApellidos());
        extraClaims.put("rolId", usuario.getRolId());
        return generarToken(extraClaims, usuario.getCorreo());
    }

    public String generarToken(Map<String, Object> extraClaims, String subject) {
        long ahora = System.currentTimeMillis();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date(ahora))
                .expiration(new Date(ahora + jwtExpirationMs))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        final String correo = extraerCorreo(token);
        return (correo.equalsIgnoreCase(userDetails.getUsername()) && !esTokenExpirado(token));
    }

    public boolean esTokenExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    private Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
