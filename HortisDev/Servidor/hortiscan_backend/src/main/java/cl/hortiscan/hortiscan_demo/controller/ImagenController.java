package cl.hortiscan.hortiscan_demo.controller;

import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RequestMapping("/api/imagen")
public class ImagenController {
  private final UsuarioService usuarioService;

  public ImagenController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  // Subir una imagen a la carpeta de un usuario
  @PostMapping("/subir/{username}")
  public ResponseEntity<?> uploadImage(@PathVariable String username, @RequestParam("file") MultipartFile file, @RequestParam String folderName) {
    System.out.println("Recibiendo solicitud de subida");
    try {
      Integer idUsuario = this.usuarioService.findIdByUsername(username);

      String filePath = usuarioService.saveImage(idUsuario, file, folderName);

      // Prepara una respuesta en formato JSON
      Map<String, String> response = new HashMap<>();
      response.put("message", "Imagen súbida con éxito ");

      // Devuelve la respuesta como JSON
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Error al guardar imagen: " + e.getMessage());
    }
  }
}
