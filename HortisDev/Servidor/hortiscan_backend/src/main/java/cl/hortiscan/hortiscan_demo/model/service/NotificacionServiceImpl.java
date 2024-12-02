// NotificacionServiceImpl.java
package cl.hortiscan.hortiscan_demo.model.service;

import cl.hortiscan.hortiscan_demo.model.dao.NotificacionDAO;
import cl.hortiscan.hortiscan_demo.model.dto.NotificacionDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Notificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.List;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionDAO notificacionDAO;

    @Override
    public Notificacion save(Notificacion notificacion) {
        return notificacionDAO.save(notificacion);
    }

    @Override
    public List<Notificacion> findAll() {
        return notificacionDAO.findAll();
    }

    @Override
    public List<Notificacion> findByIdUsuario(Integer idUsuario) {
        return notificacionDAO.findAll().stream()
                .filter(notificacion -> notificacion.getUsuario().getIdUsuario().equals(idUsuario))
                .collect(Collectors.toList());
    }

    @Override
    public Notificacion findById(Integer id) {
        return notificacionDAO.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Integer id) {
        notificacionDAO.deleteById(id);
    }

        notificacion.setFechaNotificacion(new Date());
        // Asignar usuario a notificación, si es necesario.
        Notificacion savedNotificacion = notificacionDAO.save(notificacion);
        notificacionDTO.setIdNotificacion(savedNotificacion.getIdNotificacion());
        return notificacionDTO;
    }

    // Implementación del método findNotificacionesByUsuario
    @Override
    public List<Notificacion> findNotificacionesByUsuario(Integer idUsuario) {
        return findByIdUsuario(idUsuario);
    }

    // Implementación del método deleteNotificacion
    @Override
    public void deleteNotificacion(Integer id, Integer usuarioId) {
        Notificacion notificacion = findById(id);
        if (notificacion != null && notificacion.getUsuario().getIdUsuario().equals(usuarioId)) {
            deleteById(id);
        }
    }
}
