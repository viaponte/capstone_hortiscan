package cl.hortiscan.hortiscan_demo.model.dao;

import cl.hortiscan.hortiscan_demo.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioDAO extends JpaRepository<Usuario, Integer> {
  Optional<Usuario> findByUsername(String username);
}
