<<<<<<< HEAD
=======
// NotificacionServiceImpl.java
>>>>>>> develop
package cl.hortiscan.hortiscan_demo.model.service;

import cl.hortiscan.hortiscan_demo.model.dao.NotificacionDAO;
import cl.hortiscan.hortiscan_demo.model.dto.NotificacionDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Notificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
<<<<<<< HEAD

=======
import java.util.stream.Collectors;
>>>>>>> develop
import java.util.Date;
import java.util.List;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionDAO notificacionDAO;

<<<<<<< HEAD
    // Implementación del método save
=======
>>>>>>> develop
    @Override
    public Notificacion save(Notificacion notificacion) {
        return notificacionDAO.save(notificacion);
    }

<<<<<<< HEAD
    // Implementación del método findAll
=======
>>>>>>> develop
    @Override
    public List<Notificacion> findAll() {
        return notificacionDAO.findAll();
    }

<<<<<<< HEAD
    // Implementación del método findById
=======
    @Override
    public List<Notificacion> findByIdUsuario(Integer idUsuario) {
        return notificacionDAO.findAll().stream()
                .filter(notificacion -> notificacion.getUsuario().getIdUsuario().equals(idUsuario))
                .collect(Collectors.toList());
    }

>>>>>>> develop
    @Override
    public Notificacion findById(Integer id) {
        return notificacionDAO.findById(id).orElse(null);
    }

<<<<<<< HEAD
    // Implementación del método deleteById
=======
>>>>>>> develop
    @Override
    public void deleteById(Integer id) {
        notificacionDAO.deleteById(id);
    }

<<<<<<< HEAD
    // Implementación del método saveNotificacion para el DTO
=======
>>>>>>> develop
    @Override
    public NotificacionDTO saveNotificacion(NotificacionDTO notificacionDTO) {
        Notificacion notificacion = new Notificacion();
        notificacion.setMensajeNotificacion(notificacionDTO.getMensajeNotificacion());
        notificacion.setFechaNotificacion(new Date());
<<<<<<< HEAD
        
        // Aquí deberías obtener el usuario de alguna manera
        // notificacion.setUsuario(usuario);  // Asume que puedes obtener el usuario por el ID

        Notificacion savedNotificacion = notificacionDAO.save(notificacion);
        notificacionDTO.setIdNotificacion(savedNotificacion.getIdNotificacion());
        
        return notificacionDTO;
    }
=======
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
>>>>>>> develop
}
