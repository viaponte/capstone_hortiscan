package cl.hortiscan.hortiscan_demo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

import cl.hortiscan.hortiscan_demo.model.dto.FormularioDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Imagen;
import cl.hortiscan.hortiscan_demo.model.service.CarpetaService;
import cl.hortiscan.hortiscan_demo.model.service.FormularioService;
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

  @Autowired
  private FormularioService formularioService;

  private final String ROOT_DIRECTORY = "C:\\folderToUsers";

  public ImagenController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  @PostMapping("/upload/word/{username}")
  public ResponseEntity<?> uploadWordFile(
      @PathVariable String username,
      @RequestParam("file") MultipartFile file,
      @RequestParam String folderName) {

    try {
      // Validamos o creamos la carpeta del usuario donde se guardará el archivo
      Integer idUsuario = this.usuarioService.findIdByUsername(username);
      String userFolder = ROOT_DIRECTORY + File.separator + "usuario_" + idUsuario + File.separator + folderName;
      String fileName = file.getOriginalFilename();

      File folder = new File(userFolder);
      if (!folder.exists()) {
        folder.mkdirs();
      }

      // Guardamos el archivo en el sistema de archivos local
      String filePath = userFolder + File.separator + fileName;
      File destinationFile = new File(filePath);
      file.transferTo(destinationFile);

      // Guardamos el nombre del archivo en la base de datos asociado al formulario
      FormularioDTO formularioDTO = new FormularioDTO();
      formularioDTO.setNombreFormulario(fileName);
      formularioDTO.setEstadoFormulario("Subido");
      formularioDTO.setIdUsuario(idUsuario); // Asegúrate de asociar el usuario
      formularioService.saveFormulario(formularioDTO); // Guardamos el formulario

      // Aquí añadimos el procesamiento OCR después de guardar el archivo
      String outputWordPath = userFolder + File.separator + fileName + ".docx";

      String pythonScriptPath = "src/scripts/ocr_script.py"; // Cambia esta ruta según la ubicación de tu script Python
      ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, filePath, outputWordPath);

      Process process = processBuilder.start();

      // Leer la salida estándar del proceso
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder output = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }

      // Leer la salida de error del proceso
      BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      StringBuilder errorOutput = new StringBuilder();
      while ((line = errorReader.readLine()) != null) {
        errorOutput.append(line).append("\n");
      }

      int exitCode = process.waitFor();

      if (exitCode != 0) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error al procesar word. Código de salida: " + exitCode + "\nError: " + errorOutput);
      }

      // Respuesta de éxito después del OCR
      Map<String, String> response = new HashMap<>();
      response.put("message", "Documento Word subido, guardado y procesado con éxito");
      response.put("ocrOutputPath", outputWordPath);
      response.put("ocrOutput", output.toString());

      return ResponseEntity.ok(response);

    } catch (IOException | InterruptedException e) {
      return ResponseEntity.status(500).body("Error al guardar o procesar documento Word: " + e.getMessage());
    }
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
