package cl.hortiscan.hortiscan_demo.model.dto;

import java.util.Date;

import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Formulario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenDTO {
  private Integer idImagen;
  private Formulario idFormulario;
  private Carpeta idCarpeta;
  private String rutaAlmacenamiento;
  private Date fechaCreacionImagen;
}
