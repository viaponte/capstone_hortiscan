package cl.hortiscan.hortiscan_demo.model.dao;

import cl.hortiscan.hortiscan_demo.model.entity.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagenDAO extends JpaRepository<Imagen, Integer> {
}
