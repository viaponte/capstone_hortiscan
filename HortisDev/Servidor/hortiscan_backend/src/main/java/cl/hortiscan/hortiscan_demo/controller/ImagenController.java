package cl.hortiscan.hortiscan_demo.controller;

import java.io.File;
import java.util.Date;
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
  // Subir un archivo Word a la carpeta de un usuario
  @PostMapping("/uploadWord/{username}")
  public ResponseEntity<?> uploadWord(
          @PathVariable String username,
          @RequestParam("file") MultipartFile file,
          @RequestParam String folderName) {

    System.out.println("Recibiendo solicitud de subida de documento Word");
    try {
      // Obtén el ID del usuario
      Integer idUsuario = usuarioService.findIdByUsername(username);

      // Guarda el archivo y obtén la ruta
      String filePath = usuarioService.saveWordDocument(idUsuario, file, folderName);

      // Crea una nueva entidad Imagen y guarda la información en la base de datos
      Imagen imagen = new Imagen();
      imagen.setRutaAlmacenamiento(filePath);
      imagen.setFechaCreacionImagen(new Date());

      Carpeta carpeta = carpetaService.getCarpetaIdByNombreAndUsuario(folderName, idUsuario);
      imagen.setIdCarpeta(carpeta);

      //imagenService.save(imagen);  // Guarda la entidad en la base de datos

      // Prepara la respuesta JSON
      Map<String, String> response = new HashMap<>();
      response.put("message", "Documento Word subido y guardado con éxito");
      response.put("filePath", filePath);

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Error al guardar documento Word: " + e.getMessage());
    }
  }
/*  public String saveWordDocument(Integer idUsuario, MultipartFile file, String nameFolder) {
    String userFolder = ROOT_DIRECTORY + File.separator + "usuario" + idUsuario + File.separator + nameFolder;

    // Verifica si la carpeta existe, si no, la crea
    File folder = new File(userFolder);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    // Guardar el archivo Word en la ruta del sistema de archivos
    String filePath = userFolder + File.separator + file.getOriginalFilename();
    File destiny = new File(filePath);
    try {
      file.transferTo(destiny);
    } catch (IOException e) {
      throw new RuntimeException("Error al guardar el archivo Word: " + e.getMessage());
    }

    // Obtén la entidad Carpeta usando el servicio (similar a la imagen)
    Carpeta carpeta = this.carpetaServiceImpl.getCarpetaIdByNombreAndUsuario(nameFolder, idUsuario);

    // Crear una entidad Documento (o usa una entidad Imagen si es genérica para archivos)
    Imagen documento = new Imagen();  // Puedes crear una entidad específica si es necesario
    documento.setIdFormulario(null);  // Si no tienes un formulario asignado aún
    documento.setIdCarpeta(carpeta);  // Usa la carpeta ya obtenida del servicio
    documento.setRutaAlmacenamiento(filePath);  // Almacena la ruta del archivo
    documento.setFechaCreacionImagen(new Date());  // Fecha actual

    // Guarda el documento (o imagen) en la base de datos
    imagenDAO.save(documento);

    return filePath;  // Retorna la ruta donde se guardó el documento
  }*/

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
