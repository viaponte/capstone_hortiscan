package cl.hortiscan.hortiscan_demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
  private Integer idUsuario;
  private String username;
  private List<CarpetaDTO> carpetas;
}