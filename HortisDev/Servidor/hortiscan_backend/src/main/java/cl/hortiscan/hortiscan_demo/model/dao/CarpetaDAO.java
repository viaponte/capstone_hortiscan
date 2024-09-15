package cl.hortiscan.hortiscan_demo.model.dao;

import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarpetaDAO extends JpaRepository<Carpeta, Integer> {
  List<Carpeta> findByIdUsuario(Usuario usuario);
}
