package cl.hortiscan.hortiscan_demo.model.service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.hortiscan.hortiscan_demo.model.dao.FormularioDAO;
import cl.hortiscan.hortiscan_demo.model.dao.UsuarioDAO;
import cl.hortiscan.hortiscan_demo.model.dto.FormularioDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Formulario;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;

@Service
public class FormularioServiceImpl implements FormularioService {
  @Autowired
  private UsuarioDAO usuarioDAO;

  @Autowired
  private FormularioDAO formularioDAO;

  @Autowired
  private UsuarioService usuarioService;

  @Override
  @Transactional
  public FormularioDTO saveFormulario(FormularioDTO formularioDTO) {

    // Convertir DTO a entidad
    Formulario formulario = new Formulario();
    formulario.setIdFormulario(formularioDTO.getIdFormulario());
    formulario.setNombreFormulario(formularioDTO.getNombreFormulario());
    formulario.setEstadoFormulario(formularioDTO.getEstadoFormulario());

    // Obtener la entidad Usuario para asociarla al formulario
    Usuario usuario = usuarioService.findUsuarioById(formularioDTO.getIdUsuario());
    if (usuario == null) {
      throw new RuntimeException("Usuario no encontrado con ID: " + formularioDTO.getIdUsuario());
    }
    formulario.setIdUsuario(usuario);

    System.out.println("Formulario a guardar: " + formulario);

    // Guardar el formulario en la BD
    Formulario formularioGuardado;
    try {
      formularioGuardado = this.formularioDAO.save(formulario);
      // Forzar el flush para asegurarse de que la inserción se realiza
      this.formularioDAO.flush();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error al guardar el formulario", e);
    }

    // Convertir la entidad guardada de nuevo a DTO
    FormularioDTO formularioGuardadoDTO = new FormularioDTO();
    formularioGuardadoDTO.setIdFormulario(formularioGuardado.getIdFormulario());
    formularioGuardadoDTO.setIdUsuario(formularioGuardado.getIdUsuario().getIdUsuario());
    formularioGuardadoDTO.setNombreFormulario(formularioGuardado.getNombreFormulario());
    formularioGuardadoDTO.setEstadoFormulario(formularioGuardado.getEstadoFormulario());

    return formularioGuardadoDTO;
  }

  @Override
  public void deleteFormulario(Integer idFormulario) {
    this.formularioDAO.deleteById(idFormulario);
  }

  @Override
  public List<Formulario> getAllFormularios() {
    return this.formularioDAO.findAll();
  }

  @Override
  public Formulario getFormularioIdByNameAndUsuario(String nombreFormulario, Integer idUsuario) {
    Usuario usuario = this.usuarioDAO.findById(idUsuario).orElse(null);

    if (usuario == null) {
      // Opcionalmente, maneja el caso donde el usuario no existe
      return null;
    }

    return this.formularioDAO.findByNombreFormularioAndIdUsuario(nombreFormulario, usuario)
        .orElse(null);
  }

  @Override
  public List<Formulario> findFormulariosByUsuario(Integer idUsuario) {
    Usuario usuario = usuarioDAO.findById(idUsuario)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

    return formularioDAO.findAll()
        .stream()
        .filter(formulario -> formulario.getIdUsuario().getIdUsuario().equals(usuario.getIdUsuario()))
        .collect(Collectors.toList());
  }

  private final String ROOT_DIRECTORY = "C:\\folderToUsers";
  
  @Override
  public Formulario findFormularioByPath(String path) {
    return formularioDAO.findAll()
        .stream()
        .filter(formulario -> (ROOT_DIRECTORY + File.separator + "usuario_" + formulario.getIdUsuario().getIdUsuario()
            + File.separator + formulario.getNombreFormulario()).equals(path))
        .findFirst()
        .orElse(null);
  }

}
