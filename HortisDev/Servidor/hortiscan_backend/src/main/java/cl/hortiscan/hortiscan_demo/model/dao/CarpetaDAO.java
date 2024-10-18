package cl.hortiscan.hortiscan_demo.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;

public interface CarpetaDAO extends JpaRepository<Carpeta, Integer> {
  List<Carpeta> findByIdUsuario(Usuario usuario);

  // Metodo para obtener la carpeta por nombre y id del usuario
  Optional<Carpeta> findByNombreCarpetaAndIdUsuario(String nombreCarpeta, Usuario idUsuario);

  // Metodo para encontrar una carpeta por su nombre
  Carpeta findByNombreCarpeta(String nombreCarpeta);
}
