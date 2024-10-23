package cl.hortiscan.hortiscan_demo.model.service;

import cl.hortiscan.hortiscan_demo.model.dao.NotificacionDAO;
import cl.hortiscan.hortiscan_demo.model.dto.NotificacionDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Notificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionDAO notificacionDAO;

    // Implementación del método save
    @Override
    public Notificacion save(Notificacion notificacion) {
        return notificacionDAO.save(notificacion);
    }

    // Implementación del método findAll
    @Override
    public List<Notificacion> findAll() {
        return notificacionDAO.findAll();
    }

    // Implementación del método findById
    @Override
    public Notificacion findById(Integer id) {
        return notificacionDAO.findById(id).orElse(null);
    }

    // Implementación del método deleteById
    @Override
    public void deleteById(Integer id) {
        notificacionDAO.deleteById(id);
    }

    // Implementación del método saveNotificacion para el DTO
    @Override
    public NotificacionDTO saveNotificacion(NotificacionDTO notificacionDTO) {
        Notificacion notificacion = new Notificacion();
        notificacion.setMensajeNotificacion(notificacionDTO.getMensajeNotificacion());
        notificacion.setFechaNotificacion(new Date());
        
        // Aquí deberías obtener el usuario de alguna manera
        // notificacion.setUsuario(usuario);  // Asume que puedes obtener el usuario por el ID

        Notificacion savedNotificacion = notificacionDAO.save(notificacion);
        notificacionDTO.setIdNotificacion(savedNotificacion.getIdNotificacion());
        
        return notificacionDTO;
    }
}
