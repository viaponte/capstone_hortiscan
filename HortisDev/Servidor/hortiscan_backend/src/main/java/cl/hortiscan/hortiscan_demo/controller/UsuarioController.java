package cl.hortiscan.hortiscan_demo.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Imagen;
import cl.hortiscan.hortiscan_demo.model.service.CarpetaService;
import cl.hortiscan.hortiscan_demo.model.service.ImagenService;
import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;

@RestController
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8100" })
@RequestMapping("api/usuario")
public class UsuarioController {
  private final UsuarioService usuarioService;

  @Autowired
  private CarpetaService carpetaService;

  @Autowired
  private ImagenService imagenService;

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
  public ResponseEntity<Map<String, String>> createFolderOnUser(@PathVariable String username,
      @RequestBody Map<String, String> body) {
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

    // Asigna la ruta de la carpeta
    String rutaCarpeta = ROOT_DIRECTORY + "\\" + idUsuario + "\\" + carpetaDTO.getNombreCarpeta();
    carpetaDTO.setRutaCarpeta(rutaCarpeta);

    // Llamada al servicio para crear la carpeta
    usuarioService.createFolderUser(carpetaDTO);

    // Prepara una respuesta en formato JSON
    Map<String, String> response = new HashMap<>();
    response.put("message",
        "Carpeta '" + carpetaDTO.getNombreCarpeta() + "' creada para el usuario con ID " + idUsuario);

    // Devuelve la respuesta como JSON
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{username}/carpetas")
  public ResponseEntity<List<CarpetaDTO>> getCarpetasByUsuario(@PathVariable String username) {
    Integer idUsuario = usuarioService.findIdByUsername(username);
    List<CarpetaDTO> carpetas = carpetaService.findCarpetasByUsuario(idUsuario);
    return ResponseEntity.ok(carpetas);
  }

  // Obtener el contenido de una carpeta específica
  @GetMapping("/{username}/carpeta/{nombreCarpeta}/contenido")
  public ResponseEntity<List<String>> getCarpetaContenido(@PathVariable String username,
      @PathVariable String nombreCarpeta) {
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

  @DeleteMapping("/{username}/carpeta/{nombreCarpeta}")
  public ResponseEntity<Map<String, String>> deleteCarpeta(@PathVariable String username, @PathVariable String nombreCarpeta) {
    try {
      // Se obtiene el ID del usuario
      Integer idUsuario = usuarioService.findIdByUsername(username);

      // Se obtiene el id de la carpeta por su nombre e idUsuario
      Carpeta carpeta = carpetaService.getCarpetaIdByNombreAndUsuario(nombreCarpeta, idUsuario);

      // Se captura la ID de la carpeta
      Integer idCarpeta = carpeta.getIdCarpeta();

      // Se hace path de la carpeta
      String carpetaPath = ROOT_DIRECTORY + "/usuario_" + idUsuario + "/" + nombreCarpeta;
      File carpetaLocal = new File(carpetaPath);

      if (carpetaLocal.exists() && carpetaLocal.isDirectory()) {
        deleteDirectoryRecursively(carpetaLocal); // Elimina todo el contenido de la carpeta local
      }

      // Se eliminan los registros de las imagenes en BD
      List<Imagen> imagenes = imagenService.getImagenesByCarpeta(idCarpeta);

      if (imagenes != null) {
        for (Imagen imagen : imagenes) {
          imagenService.deleteImage(imagen.getIdImagen());
        }
      }

      // Se elimina la carpeta de la base de datos
      carpetaService.deleteCarpeta(idCarpeta);

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
          "Error al eliminar carpeta");

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // Método recursivo para eliminar directorios y archivos
  private void deleteDirectoryRecursively(File file) {
    File[] contents = file.listFiles();
    if (contents != null) {
      for (File f : contents) {
        if (f.isDirectory()) {
          deleteDirectoryRecursively(f);
        } else {
          f.delete(); // Eliminar archivo
        }
      }
    }
    file.delete(); // Finalmente, eliminar el directorio
  }
}
