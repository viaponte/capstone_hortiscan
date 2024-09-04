package cl.hortiscan.hortiscan_demo.model.dao;

import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarpetaDAO extends JpaRepository<Carpeta, Integer> {
}
