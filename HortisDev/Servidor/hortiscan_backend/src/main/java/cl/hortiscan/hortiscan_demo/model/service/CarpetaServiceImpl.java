package cl.hortiscan.hortiscan_demo.model.service;

import cl.hortiscan.hortiscan_demo.model.dao.CarpetaDAO;
import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CarpetaServiceImpl implements CarpetaService {
  @Autowired
  private CarpetaDAO carpetaDAO;

  @Autowired
  private UsuarioService usuarioService;

  @Override
  public Carpeta saveCarpeta(CarpetaDTO carpetaDTO) {
    Carpeta carpeta = new Carpeta();

    // Busca la entidad Usuario por su ID
    Usuario usuario = usuarioService.findUsuarioById(carpetaDTO.getIdUsuario());

    if (usuario == null) {
      throw new RuntimeException("Usuario con ID " + carpetaDTO.getIdUsuario() + " no encontrado.");
    }

    // Asigna la entidad Usuario en lugar de un Integer
    carpeta.setIdUsuario(usuario);
    carpeta.setNombreCarpeta(carpetaDTO.getNombreCarpeta());
    carpeta.setRutaCarpeta(carpetaDTO.getRutaCarpeta());
    carpeta.setFechaCreacionCarpeta(new Date());

    // Guarda la carpeta en la base de datos
    return carpetaDAO.save(carpeta);
  }
}
