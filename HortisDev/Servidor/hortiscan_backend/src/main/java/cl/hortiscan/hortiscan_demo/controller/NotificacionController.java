package cl.hortiscan.hortiscan_demo.controller;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cl.hortiscan.hortiscan_demo.model.dto.NotificacionDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Notificacion;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;
import cl.hortiscan.hortiscan_demo.model.service.NotificacionService;
import cl.hortiscan.hortiscan_demo.model.service.UsuarioService;

// import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // Para el tipo List
import java.util.stream.Collectors; // Para Collectors

@RestController
@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:8100" })
@RequestMapping("api/notificacion")
public class NotificacionController {
    private final NotificacionService notificacionService;
    private final UsuarioService usuarioService;

    @Autowired
    public NotificacionController(NotificacionService notificacionService, UsuarioService usuarioService) {
        this.notificacionService = notificacionService;
        this.usuarioService = usuarioService;
        System.out.println("NotificacionController creado");
    }

    // Obtener notificaciones por username
    // Obtener notificaciones por username
    @GetMapping("/{username}/notificaciones")
    public ResponseEntity<List<NotificacionDTO>> getNotificacionesPorUsuario(@PathVariable String username) {
        // Encuentra el ID del usuario por su nombre de usuario
        Integer idUsuario = usuarioService.findIdByUsername(username);

        // Llama al servicio para obtener las notificaciones
        List<Notificacion> notificaciones = notificacionService.findNotificacionesByUsuario(idUsuario);

        // Mapea las notificaciones a NotificacionDTO
        List<NotificacionDTO> notificacionesDTO = notificaciones.stream()
                .map(notificacion -> {
                    NotificacionDTO dto = new NotificacionDTO();
                    dto.setIdNotificacion(notificacion.getIdNotificacion());
                    dto.setIdUsuario(notificacion.getUsuario().getIdUsuario());
                    dto.setMensajeNotificacion(notificacion.getMensajeNotificacion());
                    dto.setFechaNotificacion(notificacion.getFechaNotificacion());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(notificacionesDTO);
    }

    // Eliminar notificación por username
    @DeleteMapping("/{username}/notificacion/{idNotificacion}")
    public ResponseEntity<Map<String, String>> deleteNotificacion(@PathVariable String username,
            @PathVariable Integer idNotificacion) {
        try {
            // Se obtiene el ID del usuario
            Integer idUsuario = usuarioService.findIdByUsername(username);

            // Se elimina la notificación
            notificacionService.deleteNotificacion(idNotificacion, idUsuario);

            // Prepara una respuesta en formato JSON
            Map<String, String> response = new HashMap<>();
            response.put("message", "Notificación eliminada con éxito");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            // Prepara una respuesta en formato JSON
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error al eliminar la notificación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/{username}")
    public ResponseEntity<?> crearNotificacion(
            @PathVariable String username,
            @RequestBody NotificacionDTO notificacionDTO) {
        try {
            // Obtener el id del usuario por su username
            Integer idUsuario = usuarioService.findIdByUsername(username);
            if (idUsuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
    
            // Crear la entidad Notificacion desde el DTO
            Notificacion notificacion = new Notificacion();
            notificacion.setMensajeNotificacion(notificacionDTO.getMensajeNotificacion());
            notificacion.setFechaNotificacion(notificacionDTO.getFechaNotificacion());
            
            // Aquí se establece directamente el ID del usuario en la notificación
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(idUsuario);
            notificacion.setUsuario(usuario);
    
            // Guardar la notificación en la base de datos
            Notificacion savedNotificacion = notificacionService.save(notificacion);
    
            // Mapear a NotificacionDTO para evitar enviar las relaciones anidadas
            NotificacionDTO responseDTO = new NotificacionDTO();
            responseDTO.setIdNotificacion(savedNotificacion.getIdNotificacion());
            responseDTO.setIdUsuario(savedNotificacion.getUsuario().getIdUsuario());
            responseDTO.setMensajeNotificacion(savedNotificacion.getMensajeNotificacion());
            responseDTO.setFechaNotificacion(savedNotificacion.getFechaNotificacion());
    
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la notificación");
        }
    }
    
}
