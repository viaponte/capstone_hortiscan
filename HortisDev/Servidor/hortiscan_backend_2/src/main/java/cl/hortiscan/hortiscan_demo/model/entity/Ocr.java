package cl.hortiscan.hortiscan_demo.model.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

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
