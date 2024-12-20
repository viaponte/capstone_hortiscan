package cl.hortiscan.hortiscan_demo.model.service;

import java.util.List;

import cl.hortiscan.hortiscan_demo.model.dto.FormularioDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Formulario;

public interface FormularioService {
  FormularioDTO saveFormulario(FormularioDTO formularioDTO);
  void deleteFormulario(Integer idFormulario);
  List<Formulario> getAllFormularios();

  Formulario getFormularioIdByNameAndUsuario(String nombreFormulario, Integer idUsuario);
  List<Formulario> findFormulariosByUsuario(Integer idUsuario);
  Formulario findFormularioByPath(String path);

}
