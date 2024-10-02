package cl.hortiscan.hortiscan_demo.model.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = "idCarpeta")
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
