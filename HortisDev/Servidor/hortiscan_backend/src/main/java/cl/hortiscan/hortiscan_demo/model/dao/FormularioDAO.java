package cl.hortiscan.hortiscan_demo.model.dao;

import cl.hortiscan.hortiscan_demo.model.entity.Formulario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormularioDAO extends JpaRepository<Formulario, Integer> {
}
