package cl.hortiscan.hortiscan_demo.model.service;

import java.util.List;

import cl.hortiscan.hortiscan_demo.model.entity.Imagen;

public interface ImagenService {
  // ImagenDTO findById(Integer idImagen);
  List<Imagen> getAllImages();

  // Método para obtener las imágenes asociadas a una carpeta específica
  List<Imagen> getImagenesByCarpeta(Integer carpeta);

  // Método para eliminar la imagen por su ID
  void deleteImage(Integer idImagen);

  // Método para obtener imagen por ruta
  Imagen findByRutaAlmacenamiento(String rutaAchivo);
}
