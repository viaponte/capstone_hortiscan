package cl.hortiscan.hortiscan_demo.controller;

import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;
import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RequestMapping("api/usuario")
public class UsuarioController {
  private final UsuarioService usuarioService;

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
}
