package cl.hortiscan.hortiscan_demo.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.hortiscan.hortiscan_demo.model.dao.FormularioDAO;
import cl.hortiscan.hortiscan_demo.model.dto.FormularioDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Formulario;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;

@Service
public class FormularioServiceImpl implements FormularioService {

  @Autowired
  private FormularioDAO formularioDAO;

  @Autowired
  private UsuarioService usuarioService;

  @Override
  public FormularioDTO saveFormulario(FormularioDTO formularioDTO) {
    // Convertir DTO a entidad
    Formulario formulario = new Formulario();
    formulario.setIdFormulario(formularioDTO.getIdFormulario());
    formulario.setNombreFormulario(formularioDTO.getNombreFormulario());
    formulario.setEstadoFormulario(formularioDTO.getEstadoFormulario());

    // Obtener la entidad Usuario para asociarla al formulario
    Usuario usuario = usuarioService.findUsuarioById(formularioDTO.getIdUsuario());
    formulario.setIdUsuario(usuario);

    // Guardar el formulario en la BD
    Formulario formularioGuardado = this.formularioDAO.save(formulario);

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
    // Eliminar el formulario por ID
    this.formularioDAO.deleteById(idFormulario);
  }

  @Override
  public List<Formulario> getAllFormularios() {
    return this.formularioDAO.findAll();
  }

}
