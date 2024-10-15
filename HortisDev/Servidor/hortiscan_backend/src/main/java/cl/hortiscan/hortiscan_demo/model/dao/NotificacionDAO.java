package cl.hortiscan.hortiscan_demo.model.dao;

import cl.hortiscan.hortiscan_demo.model.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionDAO extends JpaRepository<Notificacion, Integer> {
}
