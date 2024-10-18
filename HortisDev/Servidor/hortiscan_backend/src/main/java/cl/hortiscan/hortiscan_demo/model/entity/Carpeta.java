package cl.hortiscan.hortiscan_demo.model.entity;

import java.io.Serializable;
import java.util.Date;
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
@ToString(exclude = {"idUsuario", "imagenes"})
@Table(name = "carpeta")
public class Carpeta implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_carpeta")
  private Integer idCarpeta;

  @ManyToOne
  @JoinColumn(name = "id_usuario")
  private Usuario idUsuario;

  @Column(name = "nombre_carpeta")
  private String nombreCarpeta;

  @Column(name = "ruta_carpeta")
  private String rutaCarpeta;

  @Column(name = "fecha_creacion_carpeta")
  private Date fechaCreacionCarpeta;

  // Relacion con Imagen
  @OneToMany(mappedBy = "idCarpeta", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Imagen> imagenes;

}
