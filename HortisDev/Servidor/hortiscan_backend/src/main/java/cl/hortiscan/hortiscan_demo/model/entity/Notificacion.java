package cl.hortiscan.hortiscan_demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Getter
@Setter
@ToString
@Table(name = "notificacion")
public class Notificacion implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_notificacion")
  private Integer idNotificacion;

  @ManyToOne
  @JoinColumn(name = "id_usuario", nullable = false)
  private Usuario idUsuario;

  @Column(name = "mensaje_notificacion")
  private String mensajeNotificacion;

  @Column(name = "fecha_notificacion")
  private Date fechaNotificacion;

}
