package cl.hortiscan.hortiscan_demo.model.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
  private Integer idNotificacion;
  private Integer idUsuario; // Asegúrate de que esto esté presente
  private String mensajeNotificacion;
  private Date fechaNotificacion;
}
