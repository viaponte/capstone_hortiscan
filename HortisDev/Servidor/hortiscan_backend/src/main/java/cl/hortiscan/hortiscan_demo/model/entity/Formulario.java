package cl.hortiscan.hortiscan_demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "formulario")
public class Formulario implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_formulario")
  private Integer idFormulario;

  @ManyToOne
  @JoinColumn(name = "id_usuario")
  private Usuario idUsuario;

  @Column(name = "nombre_formulario")
  private String nombreFormulario;

  @Column(name = "estado_formulario")
  private String estadoFormulario;

  // Relacion con imagen
  @OneToMany(mappedBy = "idFormulario", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Imagen> imagenes;
}
