<<<<<<< HEAD
package cl.hortiscan.hortiscan_demo.model.service;
=======
// NotificacionService.java
package cl.hortiscan.hortiscan_demo.model.service;

>>>>>>> develop
import cl.hortiscan.hortiscan_demo.model.dto.NotificacionDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Notificacion;
import java.util.List;

public interface NotificacionService {
<<<<<<< HEAD
    Notificacion save(Notificacion notificacion); // Método para guardar notificación
    List<Notificacion> findAll(); // Método para obtener todas las notificaciones
    Notificacion findById(Integer id); // Método para obtener una notificación por ID
    void deleteById(Integer id); // Método para eliminar una notificación por ID
    NotificacionDTO saveNotificacion(NotificacionDTO notificacionDTO); // Agregar este método si es necesario
=======
    Notificacion save(Notificacion notificacion);
    List<Notificacion> findAll();
    List<Notificacion> findByIdUsuario(Integer idUsuario);
    Notificacion findById(Integer id);
    void deleteById(Integer id);
    NotificacionDTO saveNotificacion(NotificacionDTO notificacionDTO);
    
    // Métodos nuevos
    List<Notificacion> findNotificacionesByUsuario(Integer idUsuario); // Agregar este método
    void deleteNotificacion(Integer id, Integer usuarioId); // Agregar este método
>>>>>>> develop
}
