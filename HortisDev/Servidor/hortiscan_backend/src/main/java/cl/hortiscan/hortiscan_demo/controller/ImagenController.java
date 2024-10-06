package cl.hortiscan.hortiscan_demo.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Imagen;
import cl.hortiscan.hortiscan_demo.model.service.CarpetaService;
import cl.hortiscan.hortiscan_demo.model.service.ImagenService;
import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;

@RestController
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8100" })
@RequestMapping("/api/imagen")
public class ImagenController {
  private final UsuarioService usuarioService;

  @Autowired
  private CarpetaService carpetaService;

  @Autowired
  private ImagenService imagenService;

  private final String ROOT_DIRECTORY = "C:\\folderToUsers";

  public ImagenController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  // Subir una imagen a la carpeta de un usuario
  @PostMapping("/subir/{username}")
  public ResponseEntity<?> uploadImage(@PathVariable String username, @RequestParam("file") MultipartFile file,
      @RequestParam String folderName) {
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

  // Método para eliminar una imagen
  @DeleteMapping("/{username}/carpeta/{nombreCarpeta}/imagen/{fileName}")
  public ResponseEntity<Map<String, String>> deleteImagen(
      @PathVariable String username,
      @PathVariable String nombreCarpeta,
      @PathVariable String fileName) {
    try {
      // Se obtiene el ID del usuario por el nombre de usuario
      Integer idUsuario = usuarioService.findIdByUsername(username);

      // Se obtiene la carpeta a través del nombre e ID del usuario
      Carpeta carpeta = carpetaService.getCarpetaIdByNombreAndUsuario(nombreCarpeta, idUsuario);

      if (carpeta == null) {
        // Prepara una respuesta en formato JSON
        Map<String, String> response = new HashMap<>();
        response.put("error",
            "Carpeta no encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Se obtiene la imagen de la BD mediante su ruta de almacenamiento
      String filePath = ROOT_DIRECTORY + "\\usuario_" + idUsuario + "\\" + nombreCarpeta + "\\" + fileName;
      Imagen imagen = imagenService.findByRutaAlmacenamiento(filePath);

      if (imagen == null) {
        // Prepara una respuesta en formato JSON
        Map<String, String> response = new HashMap<>();
        response.put("error",
            "Imagen no encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Se elimina la imagen de la carpeta local
      File imagenLocal = new File(filePath);

      System.out.println(imagen);

      imagenLocal.delete();

      // Se captura el ID de la imagen
      Integer idImagen = imagen.getIdImagen();

      // Se elimina el registro de la imagen de la BD
      imagenService.deleteImage(idImagen);

      // Prepara una respuesta en formato JSON
      Map<String, String> response = new HashMap<>();
      response.put("message",
          "Carpeta '" + nombreCarpeta + "' eliminada con exito");

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      e.printStackTrace();
      // Prepara una respuesta en formato JSON
      Map<String, String> response = new HashMap<>();
      response.put("error",
          "Error al eliminar imagen");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}
