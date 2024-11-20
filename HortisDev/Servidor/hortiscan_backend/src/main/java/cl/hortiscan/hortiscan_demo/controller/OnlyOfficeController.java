package cl.hortiscan.hortiscan_demo.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.hortiscan.hortiscan_demo.configuration.OnlyOfficeConfig;
import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/document")
public class OnlyOfficeController {
  @Value("${onlyoffice.jwt.secret}")
  private String jwtSecret;

  @Value("${onlyoffice.url}")
  private String onlyOfficeUrl;

  @Value("${onlyoffice.document.urlBase}")
  private String documentUrlBase;

  @Autowired
  private UsuarioService usuarioService;

  private final String ROOT_DIRECTORY = "C:\\folderToUsers";

  @GetMapping("/edit/{username}/{nombreCarpeta}/{fileName}")
  public ResponseEntity<Map<String, Object>> editDocument(@PathVariable String username,
      @PathVariable String nombreCarpeta, @PathVariable String fileName) {

    Map<String, Object> response = new HashMap<>();

    try {
      // Se obtiene el ID del usuario
      Integer idUsuario = usuarioService.findIdByUsername(username);

      // Se construye la ruta del archivo
      String filePath = this.ROOT_DIRECTORY + "\\usuario_" + idUsuario + "\\" + nombreCarpeta + "\\" + fileName;

      // Se verifica si el archivo existe
      File file = new File(filePath);
      if (!file.exists()) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }

      // Se genera la clave única
      String documentKey = this.generateDocumentKey(username, nombreCarpeta, fileName);

      OnlyOfficeConfig config = new OnlyOfficeConfig();
      config.setDocumentType("text");

      Map<String, Object> document = new HashMap<>();
      document.put("title", fileName);
      document.put("url",
          documentUrlBase + "/api/document/download/" + username + "/" + nombreCarpeta + "/" + fileName);
      document.put("fileType", this.getFileExtension(fileName));
      document.put("key", documentKey);

      // Configurar los permisos
      Map<String, Object> permissions = new HashMap<>();
      permissions.put("edit", true);
      permissions.put("download", true);
      permissions.put("print", true);
      permissions.put("review", true);
      permissions.put("comment", true);
      permissions.put("fillForms", true);
      permissions.put("modifyFilter", true);
      permissions.put("modifyContentControl", true);
      permissions.put("copy", true);
      permissions.put("rename", true);
      permissions.put("changeHistory", true);
      permissions.put("forceSave", true);
      document.put("permissions", permissions);

      config.setDocument(document);

      Map<String, Object> editorConfig = new HashMap<>();
      editorConfig.put("mode", "edit");
      editorConfig.put("callbackUrl", documentUrlBase + "/api/document/callback?fileName=" + fileName + "&username="
          + username + "&nombreCarpeta=" + nombreCarpeta);

      Map<String, Object> user = new HashMap<>();
      user.put("id", idUsuario.toString());
      user.put("name", username);

      editorConfig.put("user", user);
      config.setEditorConfig(editorConfig);

      // Convertir el objeto config en un Map
      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, Object> configMap = objectMapper.convertValue(config, Map.class);

      // Remover el campo "token" si existe
      configMap.remove("token");

      // Generar el token JWT sobre el configMap
      Algorithm algorithm = Algorithm.HMAC256(this.jwtSecret);
      String token = JWT.create()
          .withPayload(configMap)
          .sign(algorithm);

      // Establecer el token en el objeto config
      config.setToken(token);

      response.put("config", config);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/download/{username}/{nombreCarpeta}/{fileName}")
  public ResponseEntity<Resource> downloadDocument(
      @PathVariable String username,
      @PathVariable String nombreCarpeta,
      @PathVariable String fileName) {
    try {
      // Se obtiene el ID del usuario
      Integer idUsuario = usuarioService.findIdByUsername(username);

      // Se construye la ruta del archivo
      String filePath = this.ROOT_DIRECTORY + "\\usuario_" + idUsuario + "\\" + nombreCarpeta + "\\" + fileName;
      File file = new File(filePath);

      // Se verifica si el archivo existe
      if (!file.exists()) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }

      UrlResource resource = new UrlResource(file.toURI());

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

      return ResponseEntity.ok()
          .headers(headers)
          .contentLength(file.length())
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(resource);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/callback")
  public ResponseEntity<?> handleCallback(
      @RequestParam("username") String username,
      @RequestParam("nombreCarpeta") String nombreCarpeta,
      @RequestParam("fileName") String fileName,
      @RequestBody Map<String, Object> body,
      HttpServletRequest request) {

    try {
      // Obtener el token del encabezado Authorization
      String authorizationHeader = request.getHeader("Authorization");
      String token = null;

      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        token = authorizationHeader.substring(7);
      } else {
        // Si no se encuentra el token, responder con error
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      }

      // Validar el token JWT
      Algorithm algorithm = Algorithm.HMAC256(this.jwtSecret);
      JWTVerifier verifier = JWT.require(algorithm).build();
      DecodedJWT decodedJWT = verifier.verify(token);

      // Procesar la respuesta
      int status = (int) body.get("status");

      if (status == 2 || status == 3 || status == 6 || status == 7) {
        String downloadUri = (String) body.get("url");

        // Obtener la ruta original del archivo
        Integer idUsuario = usuarioService.findIdByUsername(username);

        // Construir la ruta del archivo
        String filePath = this.ROOT_DIRECTORY + "\\usuario_" + idUsuario + "\\" + nombreCarpeta + "\\" + fileName;

        // Descargar el archivo modificado
        RestTemplate restTemplate = new RestTemplate();
        byte[] fileBytes = restTemplate.getForObject(downloadUri, byte[].class);

        // Guardar el archivo
        Path path = Paths.get(filePath);
        Files.write(path, fileBytes);
      }

      Map<String, Object> response = new HashMap<>();
      response.put("error", 0);

      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (Exception e) {
      e.printStackTrace();
      Map<String, Object> response = new HashMap<>();
      response.put("error", 1);
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Método para obtener la extensión del archivo
  private String getFileExtension(String fileName) {
    int dotIndex = fileName.lastIndexOf('.');
    if (dotIndex == -1) {
      return ""; // No hay extensión
    }
    return fileName.substring(dotIndex + 1);
  }

  // Método para generar clave única
  private String generateDocumentKey(String username, String nombreCarpeta, String fileName) {
    return UUID.nameUUIDFromBytes((username + "_" + nombreCarpeta + "_" + fileName).getBytes()).toString();
  }
}