package cl.hortiscan.hortiscan_demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@ToString
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
