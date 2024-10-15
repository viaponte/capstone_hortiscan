package cl.hortiscan.hortiscan_demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormularioDTO {
  private Integer idFormulario;
  private Integer idUsuario;
  private String nombreFormulario;
  private String estadoFormulario;
  private List<ImagenDTO> imagenes;
}
