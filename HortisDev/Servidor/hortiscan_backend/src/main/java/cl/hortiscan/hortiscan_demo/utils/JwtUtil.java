package cl.hortiscan.hortiscan_demo.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

  // Clave secreta utilizada para firmar el token JWT
  @Value("${onlyoffice.jwt.secret}")
    private String secret;

  /**
   * Extrae el nombre de usuario del token JWT.
   *
   * @param token el token JWT
   * @return el nombre de usuario extraído del token
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extrae la fecha de expiración del token JWT.
   *
   * @param token el token JWT
   * @return la fecha de expiración del token
   */
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extrae un reclamo (claim) específico del token JWT.
   *
   * @param token el token JWT
   * @param claimsResolver la función para resolver el reclamo
   * @param <T> el tipo del reclamo
   * @return el valor del reclamo
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extrae todos los reclamos (claims) del token JWT.
   *
   * @param token el token JWT
   * @return todos los reclamos del token
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
  }

  /**
   * Verifica si el token JWT ha expirado.
   *
   * @param token el token JWT
   * @return true si el token ha expirado, false en caso contrario
   */
  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * Genera un token JWT basado en los detalles del usuario.
   *
   * @param userDetails los detalles del usuario
   * @return el token JWT generado
   */
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();  // Puedes agregar información adicional al token aquí
    return createToken(claims, userDetails.getUsername());
  }

  /**
   * Crea un token JWT con los reclamos y el nombre de usuario proporcionados.
   *
   * @param claims los reclamos (claims) que se incluirán en el token
   * @param subject el nombre de usuario que será el sujeto del token
   * @return el token JWT creado
   */
  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
            .setClaims(claims)  // Establece los reclamos del token
            .setSubject(subject)  // Establece el nombre de usuario como sujeto
            .setIssuedAt(new Date(System.currentTimeMillis()))  // Fecha de emisión del token
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))  // Establece la expiración del token (10 horas)
            .signWith(SignatureAlgorithm.HS256, secret)  // Firma el token con el algoritmo HS256 y la clave secreta
            .compact();
  }

  /**
   * Valida si el token JWT es correcto y pertenece al usuario proporcionado.
   *
   * @param token el token JWT
   * @param userDetails los detalles del usuario
   * @return true si el token es válido, false en caso contrario
   */
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));  // Verifica que el token pertenezca al usuario y no haya expirado
  }
}