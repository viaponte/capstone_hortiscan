package cl.hortiscan.hortiscan_demo.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
=======
import org.springframework.http.CacheControl;
>>>>>>> develop
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;
import cl.hortiscan.hortiscan_demo.negocio.WordToPdfLibreOfficeService;

@RestController
@RequestMapping("/api/convert")
public class ConversionController {
  private final String ROOT_DIRECTORY = "C:\\folderToUsers";

  @Autowired
  private WordToPdfLibreOfficeService wordToPdfLibreOfficeService;

  @Autowired
  private UsuarioService usuarioService;

  @GetMapping("/{username}/carpeta/{nombreCarpeta}/archivo/{fileName}/libreoffice")
  public ResponseEntity<byte[]> getWordAsPdf(
      @PathVariable String username,
      @PathVariable String nombreCarpeta,
      @PathVariable String fileName) {
    try {
      Integer idUsuario = this.usuarioService.findIdByUsername(username);
      String filePath = ROOT_DIRECTORY + "\\usuario_" + idUsuario + "\\" + nombreCarpeta + "\\" + fileName;
      Path wordFilePath = Paths.get(filePath);

      if (!Files.exists(wordFilePath)) {
        return ResponseEntity.status(404).body(null); // Archivo no encontrado
      }

<<<<<<< HEAD
=======
      // Esperar hasta que el archivo sea actualizado (hasta un tiempo máximo)
      int maxWaitTime = 5000; // Tiempo máximo de espera en milisegundos
      int waitedTime = 0;
      int waitInterval = 500; // Intervalo de espera entre comprobaciones

      while (waitedTime < maxWaitTime) {
        long lastModified = Files.getLastModifiedTime(wordFilePath).toMillis();
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastModified < 1000) { // Si el archivo fue modificado en el último segundo
          break;
        } else {
          // Esperar y volver a comprobar
          Thread.sleep(waitInterval);
          waitedTime += waitInterval;
        }
      }

>>>>>>> develop
      ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
      try (InputStream wordInputStream = Files.newInputStream(wordFilePath)) {
        wordToPdfLibreOfficeService.convertWordToPdf(wordInputStream, pdfOutputStream);
      }

      byte[] pdfBytes = pdfOutputStream.toByteArray();
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDisposition(
          ContentDisposition.inline().filename(fileName.replaceAll("\\.docx?$", ".pdf")).build());
<<<<<<< HEAD
=======
      headers.setCacheControl(CacheControl.noCache().mustRevalidate());
      headers.setPragma("no-cache");
      headers.setExpires(0);
>>>>>>> develop

      return ResponseEntity
          .ok()
          .headers(headers)
          .body(pdfBytes);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity
          .status(500)
          .body(null);
    }
  }
<<<<<<< HEAD
=======

>>>>>>> develop
}