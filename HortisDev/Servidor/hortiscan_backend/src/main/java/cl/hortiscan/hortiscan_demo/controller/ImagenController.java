package cl.hortiscan.hortiscan_demo.controller;

import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RequestMapping("/api/imagen")
public class ImagenController {
  private UsuarioService usuarioService;

  public ImagenController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  // Subir una imagen a la carpeta de un usuario
  @PostMapping("/subir/{idUsuario}")
  public ResponseEntity<?> uploadImage(@PathVariable Integer idUsuario, @RequestParam("file") MultipartFile file, @RequestParam String folderName) {
    try {
      String filePath = usuarioService.saveImage(idUsuario, file, folderName);
      return ResponseEntity.ok("Imagen guardada en: " + filePath);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Error al guardar imagen: " + e.getMessage());
    }
  }
}
