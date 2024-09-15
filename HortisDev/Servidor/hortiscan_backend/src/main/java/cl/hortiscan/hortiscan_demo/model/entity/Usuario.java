package cl.hortiscan.hortiscan_demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
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
  @OneToMany(mappedBy = "idUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Carpeta> carpetas;

  // Relacion con Notificacion
  @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Notificacion> notificaciones;

  // Relacion con Formulario
  @OneToMany(mappedBy = "idUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Formulario> formularios;
}
