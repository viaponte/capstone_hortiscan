package cl.hortiscan.hortiscan_demo.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.hortiscan.hortiscan_demo.model.entity.Formulario;
import cl.hortiscan.hortiscan_demo.model.service.FormularioService;
import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;

@RestController
@RequestMapping("/api/formulario")
public class FormularioController {
  @Autowired
  private FormularioService formularioService;

  @Autowired
  private UsuarioService usuarioService;

  private final String ROOT_DIRECTORY = "C:\\folderToUsers";

  @DeleteMapping("{username}/delete/{folderName}/{formularioName}")
  public ResponseEntity<?> deleteFormulario(
      @PathVariable String username,
      @PathVariable String folderName,
      @PathVariable String formularioName) {
    try {
      // Obtener el ID del usuario
      Integer idUsuario = usuarioService.findIdByUsername(username);

      // Eliminar el archivo local
      String filePath = ROOT_DIRECTORY + "\\usuario_" + idUsuario + "\\" + folderName + "\\" + formularioName;
      File formularioLocal = new File(filePath);
      formularioLocal.delete();

      // Obtener el formulario antes de eliminarlo
      Formulario formulario = formularioService.getFormularioIdByNameAndUsuario(formularioName, idUsuario);

      if (formulario == null) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Formulario no encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Eliminar el registro de la BD
      Integer idFormulario = formulario.getIdFormulario();
      formularioService.deleteFormulario(idFormulario);

      // Preparar la respuesta
      Map<String, String> response = new HashMap<>();
      response.put("message", "Formulario '" + formularioName + "' eliminado con éxito");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      e.printStackTrace();
      Map<String, String> response = new HashMap<>();
      response.put("error", "Error al eliminar formulario");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

}
