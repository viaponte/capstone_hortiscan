package cl.hortiscan.hortiscan_demo.controller;

import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/usuario")
public class UsuarioController {
  private final UsuarioService usuarioService;

  public UsuarioController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
    System.out.println("UsuarioController creado");
  }

  // Validar o crear carpeta del usuario
  @PostMapping("/validar-carpeta/{idUsuario}")
  public ResponseEntity<?> validateOrCreateFolder(@PathVariable Integer idUsuario) {
    usuarioService.validateOrCreateFolder(idUsuario);
    return ResponseEntity.ok("Carpeta validada/creada para el usuario con ID " + idUsuario);
  }

  // Crear una nueva carpeta dentro de la carpeta de un usuario
  @PostMapping("/{idUsuario}/crear-carpeta")
  public ResponseEntity<?> createFolderOnUser(@PathVariable Integer idUsuario, @RequestParam String folderName) {
    usuarioService.createFolderUser(idUsuario, folderName);
    return ResponseEntity.ok("Carpeta '" + folderName + "' creada para el usuario con ID " + idUsuario);
  }
}
