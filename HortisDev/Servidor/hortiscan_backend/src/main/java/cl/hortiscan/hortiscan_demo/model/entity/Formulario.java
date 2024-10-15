package cl.hortiscan.hortiscan_demo.model.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

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
