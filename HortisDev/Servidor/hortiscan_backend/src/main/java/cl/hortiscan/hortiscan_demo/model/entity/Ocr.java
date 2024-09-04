package cl.hortiscan.hortiscan_demo.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Entity
@Data
@ToString
@Table(name = "ocr")
public class Ocr implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_ocr")
  private Integer idOcr;

  @OneToOne
  @JoinColumn(name = "id_imagen")
  private Imagen idImagen;

  @Column(name = "texto_extraido")
  private String textoExtraido;

  @Column(name = "precision_ocr")
  private Integer precisionOcr;
}
