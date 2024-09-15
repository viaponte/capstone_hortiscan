package cl.hortiscan.hortiscan_demo.model.service;

import cl.hortiscan.hortiscan_demo.model.dao.CarpetaDAO;
import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

  @Override
  public List<CarpetaDTO> findCarpetasByUsuario(Integer idUsuario) {
    // Busca al usuario por su ID
    Usuario usuario = usuarioService.findUsuarioById(idUsuario);

    if (usuario == null) {
      throw new RuntimeException("Usuario con ID " + idUsuario + " no encontrado.");
    }

    List<Carpeta> carpetas = carpetaDAO.findByIdUsuario(usuario);

    return carpetas.stream().map(carpeta -> new CarpetaDTO(
            carpeta.getIdCarpeta(),
            carpeta.getIdUsuario().getIdUsuario(),
            carpeta.getNombreCarpeta(),
            carpeta.getRutaCarpeta(),
            carpeta.getFechaCreacionCarpeta(),
            null
    )).collect(Collectors.toList());
  }

  @Override
  public List<String> getContenidoCarpeta(Integer idUsuario, String nombreCarpeta) {
    final String ROOT_DIRECTORY = "C:\\folderToUsers";

    // Ruta en el sistema de archivos donde deberían existir las carpetas
    String carpetaRuta = ROOT_DIRECTORY + File.separator + "usuario_" + idUsuario + File.separator + nombreCarpeta;

    System.out.println(carpetaRuta);

    File carpeta = new File(carpetaRuta);

    // Verifica si la carpeta existe y es un directorio
    if (!carpeta.exists() || !carpeta.isDirectory()) {
      throw new RuntimeException("La carpeta no existe o no es un directorio válido.");
    }

    // Obtener la lista de archivos en la carpeta
    String[] archivos = carpeta.list();

    if (archivos == null) {
      throw new RuntimeException("No se pudo leer el contenido de la carpeta.");
    }

    System.out.println(carpetaRuta + File.separator + archivos[0]);

    return Arrays.asList(archivos);
  }
}
