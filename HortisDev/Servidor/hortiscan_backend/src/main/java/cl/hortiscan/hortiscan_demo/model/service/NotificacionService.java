// NotificacionService.java
package cl.hortiscan.hortiscan_demo.model.service;

import java.util.List;

import cl.hortiscan.hortiscan_demo.model.dto.NotificacionDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Notificacion;

public interface NotificacionService {
    Notificacion save(Notificacion notificacion);
    List<Notificacion> findAll();
    List<Notificacion> findByIdUsuario(Integer idUsuario);
    Notificacion findById(Integer id);
    void deleteById(Integer id);
    NotificacionDTO saveNotificacion(NotificacionDTO notificacionDTO);
    
    // Métodos nuevos
    List<Notificacion> findNotificacionesByUsuario(Integer idUsuario); // Agregar este método
    void deleteNotificacion(Integer id, Integer usuarioId); // Agregar este método
}
