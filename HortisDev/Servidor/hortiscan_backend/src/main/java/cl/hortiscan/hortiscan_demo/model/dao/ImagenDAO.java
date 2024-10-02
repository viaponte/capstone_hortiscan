package cl.hortiscan.hortiscan_demo.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Imagen;


public interface ImagenDAO extends JpaRepository<Imagen, Integer> {
  // Método para obtener todas las imagenes por una carpeta específica
  List<Imagen> findByIdCarpeta(Carpeta carpeta);

  // Método para encontrar una imagen por su ruta de almacenamiento
  Optional<Imagen> findByRutaAlmacenamiento(String rutaAlmacenamiento);
}
