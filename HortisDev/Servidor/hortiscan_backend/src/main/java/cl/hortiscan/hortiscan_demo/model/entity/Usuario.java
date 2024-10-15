package cl.hortiscan.hortiscan_demo.model.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = {"carpetas", "notificaciones", "formularios" })
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
