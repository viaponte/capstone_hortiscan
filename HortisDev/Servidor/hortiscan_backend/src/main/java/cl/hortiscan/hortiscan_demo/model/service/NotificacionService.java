package cl.hortiscan.hortiscan_demo.model.service;
import cl.hortiscan.hortiscan_demo.model.dto.NotificacionDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Notificacion;
import java.util.List;

public interface NotificacionService {
    Notificacion save(Notificacion notificacion); // Método para guardar notificación
    List<Notificacion> findAll(); // Método para obtener todas las notificaciones
    Notificacion findById(Integer id); // Método para obtener una notificación por ID
    void deleteById(Integer id); // Método para eliminar una notificación por ID
    NotificacionDTO saveNotificacion(NotificacionDTO notificacionDTO); // Agregar este método si es necesario
}
