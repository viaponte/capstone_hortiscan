package cl.hortiscan.hortiscan_demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRegistroDTO extends UsuarioDTO {
  private String username;
  private String password;
}
