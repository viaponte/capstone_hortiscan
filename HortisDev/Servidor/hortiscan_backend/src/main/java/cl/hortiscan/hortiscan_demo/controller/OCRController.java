package cl.hortiscan.hortiscan_demo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
public class OCRController {
  @PostMapping("/process-ocr")
  public ResponseEntity<String> processImage(@RequestParam("image") MultipartFile image) {
    try {
      // 1. Guardar la imagen temporalmente
      String tempImagePath = System.getProperty("java.io.tmpdir") + UUID.randomUUID() + "_"
          + image.getOriginalFilename();

      File tempImageFile = new File(tempImagePath);
      image.transferTo(tempImageFile);

      // 2. Ruta de salida del archivo Word
      String outputWordPath = System.getProperty("java.io.tmpdir") + UUID.randomUUID() + "_texto_extraido.docx";

      // 3. Ejecutar el script Python con ProcessBuilder
      String pythonScriptPath = "src\\scripts\\ocr_script.py"; // Cambia esta ruta según la ubicación de tu script
                                                               // Python

      ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, tempImagePath, outputWordPath);

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

      if (exitCode == 0) {
        return ResponseEntity.ok("Archivo Word creado en: " + outputWordPath + "\nSalida: " + output);
      } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error al procesar word. Código de salida: " + exitCode + "\nError: " + errorOutput);
      }

    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la imagen.");
    }
  }
}