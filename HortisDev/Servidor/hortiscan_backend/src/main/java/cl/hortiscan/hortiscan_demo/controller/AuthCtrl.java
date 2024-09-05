package cl.hortiscan.hortiscan_demo.controller;

import cl.hortiscan.hortiscan_demo.model.auth.AuthRequest;
import cl.hortiscan.hortiscan_demo.model.auth.AuthResponse;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioDTO;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioRegistroDTO;
import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;
import cl.hortiscan.hortiscan_demo.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin("http://localhost:4200")
@RequestMapping("/api/auth")
public class AuthCtrl {
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UsuarioService usuarioService;

  @Autowired
  private JwtUtil jwtUtil;

  /*
  * Endpoint para manejar el login
  * @param authRequest - Datos de autenticacion enviados por el cliente
  * @return JWT en caso de autenticacion exitosa
  * @throws Exception si las credenciales son incorrectas
  */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) throws Exception {
    try {
      // Autenticar al usuario usando los detalles proporcionados
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
      System.out.println("Conexión valida");
    } catch (Exception e) {
      throw new Exception("Invalid username or password", e);
    }

    // Cargar los detalles del usuario y generar el JWT
    final UserDetails userDetails = usuarioService.loadUserByUsername(authRequest.getUsername());
    final String jwt = jwtUtil.generateToken(userDetails);

    return ResponseEntity.ok(new AuthResponse(jwt));
  }

  /*
  * Endpoint para manejar el registro de nuevos usuarios
  * @param usuarioRegistroDTO - Datos del usuario que se va a registrar
  * @return
  */
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody UsuarioRegistroDTO usuarioRegistroDTO) throws Exception {
    System.out.println(usuarioRegistroDTO);
    UsuarioDTO savedUser = usuarioService.saveUser(usuarioRegistroDTO);
    return ResponseEntity.ok(savedUser);
  }
}
