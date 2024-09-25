package cl.hortiscan.hortiscan_demo.model.dao;

import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarpetaDAO extends JpaRepository<Carpeta, Integer> {
  List<Carpeta> findByIdUsuario(Usuario usuario);

  // Metodo para obtener la carpeta por nombre y id del usuario
  Optional<Carpeta> findByNombreCarpetaAndIdUsuario(String nombreCarpeta, Usuario idUsuario);

}
