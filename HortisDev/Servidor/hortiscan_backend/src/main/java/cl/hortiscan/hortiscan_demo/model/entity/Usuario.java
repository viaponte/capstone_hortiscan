package cl.hortiscan.hortiscan_demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@Getter
@Setter
@Entity
@ToString
@Table(name = "usuario")
public class Usuario implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_usuario")
  private Integer idUsuario;

  @Column(name = "nombre_usuario", nullable = false, unique  = true)
  private String username;

  @Column(name = "password_usuario", nullable = false)
  private String password;

  // Relacion con Carpeta
  @OneToMany(mappedBy = "usuario")
  private List<Carpeta> carpetas;

  // Relacion con Notificacion
  @OneToMany(mappedBy = "usuario")
  private List<Notificacion> notificaciones;

  // Relacion con Formulario
  @OneToMany(mappedBy = "usuario")
  private List<Formulario> formularios;
}
