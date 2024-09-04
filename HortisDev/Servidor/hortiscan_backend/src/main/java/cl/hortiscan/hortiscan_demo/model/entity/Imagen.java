package cl.hortiscan.hortiscan_demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@ToString
@Table(name = "imagen")
public class Imagen implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_imagen")
  private Integer idImagen;

  @ManyToOne
  @JoinColumn(name = "id_formulario")
  private Formulario idFormulario;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_carpeta")
  private Carpeta idCarpeta;

  @Column(name = "ruta_almacenamiento")
  private String rutaAlmacenamiento;

  @Column(name = "fecha_creacion_imagen")
  private Date fechaCreacionImagen;

}
