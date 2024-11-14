package cl.hortiscan.hortiscan_demo.model.dao;

import cl.hortiscan.hortiscan_demo.model.entity.Formulario;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FormularioDAO extends JpaRepository<Formulario, Integer> {
  Optional<Formulario> findByNombreFormularioAndIdUsuario(String nombreFormulario, Usuario idUsuario);
}
