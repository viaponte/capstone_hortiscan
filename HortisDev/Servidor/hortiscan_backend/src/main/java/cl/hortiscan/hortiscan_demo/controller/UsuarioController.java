package cl.hortiscan.hortiscan_demo.controller;

import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;
import cl.hortiscan.hortiscan_demo.model.service.CarpetaService;
import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RequestMapping("api/usuario")
public class UsuarioController {
  private final UsuarioService usuarioService;

  @Autowired
  private CarpetaService carpetaService;

  private final String ROOT_DIRECTORY = "C:\\folderToUsers";

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
  @PostMapping("/{username}/crear-carpeta")
  public ResponseEntity<?> createFolderOnUser(@PathVariable String username, @RequestBody Map<String, String> body) {
    // Encuentra el ID del usuario por su nombre de usuario
    Integer idUsuario = usuarioService.findIdByUsername(username);

    // Crea una nueva instancia de CarpetaDTO
    CarpetaDTO carpetaDTO = new CarpetaDTO();

    // Obtén el nombre de la carpeta desde el body del request
    String folderName = body.get("folderName");

    // Completa el DTO con los datos necesarios
    carpetaDTO.setIdUsuario(idUsuario);
    carpetaDTO.setNombreCarpeta(folderName);
    carpetaDTO.setFechaCreacionCarpeta(new Date());

    // Asigna la ruta de la carpeta (ejemplo, la puedes definir como quieras)
    String rutaCarpeta = ROOT_DIRECTORY + "\\" + idUsuario + "\\" + carpetaDTO.getNombreCarpeta();
    carpetaDTO.setRutaCarpeta(rutaCarpeta);

    // Llamada al servicio para crear la carpeta
    usuarioService.createFolderUser(carpetaDTO);

    return ResponseEntity.ok("Carpeta '" + carpetaDTO.getNombreCarpeta() + "' creada para el usuario con ID " + idUsuario);
  }

  @GetMapping("/{username}/carpetas")
  public ResponseEntity<List<CarpetaDTO>> getCarpetasByUsuario(@PathVariable String username) {
    Integer idUsuario = usuarioService.findIdByUsername(username);
    List<CarpetaDTO> carpetas = carpetaService.findCarpetasByUsuario(idUsuario);
    return ResponseEntity.ok(carpetas);
  }

  // Obtener el contenido de una carpeta específica
  @GetMapping("/{username}/carpeta/{nombreCarpeta}/contenido")
  public ResponseEntity<List<String>> getCarpetaContenido(@PathVariable String username, @PathVariable String nombreCarpeta) {
    // Encuentra el ID del usuario por su nombre de usuario
    Integer idUsuario = usuarioService.findIdByUsername(username);

    // Llama al servicio para obtener los nombres de los archivos en la carpeta
    List<String> archivos = carpetaService.getContenidoCarpeta(idUsuario, nombreCarpeta);

    return ResponseEntity.ok(archivos);
  }

  @GetMapping("/{username}/carpeta/{nombreCarpeta}/archivo/{fileName}")
  public ResponseEntity<Resource> getArchivo(
          @PathVariable String username,
          @PathVariable String nombreCarpeta,
          @PathVariable String fileName) {
    try {
      Integer idUsuario = usuarioService.findIdByUsername(username);

      // Ruta absoluta del archivo
      Path filePath = Paths.get("C:/folderToUsers/usuario_" + idUsuario + "/" + nombreCarpeta + "/" + fileName);
      Resource resource = new UrlResource(filePath.toUri());

      if (!resource.exists() || !resource.isReadable()) {
        return ResponseEntity.notFound().build();
      }

      // Determina el tipo de contenido del archivo
      String contentType = Files.probeContentType(filePath);
      if (contentType == null) {
        contentType = "application/octet-stream";
      }

      return ResponseEntity.ok()
              .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
              .header(HttpHeaders.CONTENT_TYPE, contentType)
              .body(resource);

    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
