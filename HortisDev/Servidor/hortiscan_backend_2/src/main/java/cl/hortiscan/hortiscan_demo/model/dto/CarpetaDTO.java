package cl.hortiscan.hortiscan_demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarpetaDTO {
  private Integer idCarpeta;
  private Integer idUsuario;
  private String nombreCarpeta;
  private String rutaCarpeta;
  private Date fechaCreacionCarpeta;
  private List<ImagenDTO> imagenes;
}
