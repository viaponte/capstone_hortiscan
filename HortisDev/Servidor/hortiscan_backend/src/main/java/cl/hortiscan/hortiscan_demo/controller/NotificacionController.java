package cl.hortiscan.hortiscan_demo.controller;

import cl.hortiscan.hortiscan_demo.model.dto.NotificacionDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Notificacion;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;
import cl.hortiscan.hortiscan_demo.model.service.NotificacionService;
import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    private final UsuarioService usuarioService;

    @Autowired
    private NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService, UsuarioService usuarioService) {
        this.notificacionService = notificacionService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/{username}")
    public ResponseEntity<?> crearNotificacion(@RequestBody NotificacionDTO notificacionDTO) {
        try {
            // Obtener el usuario por su idUsuario desde el DTO
            Usuario usuario = usuarioService.findUsuarioById(notificacionDTO.getIdUsuario());
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
    
            // Crear la entidad Notificacion desde el DTO
            Notificacion notificacion = new Notificacion();
            notificacion.setMensajeNotificacion(notificacionDTO.getMensajeNotificacion());
            notificacion.setFechaNotificacion(notificacionDTO.getFechaNotificacion());
            notificacion.setUsuario(usuario);  // Asociamos el usuario encontrado
    
            // Guardar la notificación en la base de datos
            Notificacion savedNotificacion = notificacionService.save(notificacion);
    
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNotificacion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la notificación");
        }
    }
    

}