package cl.hortiscan.hortiscan_demo.model.service;

import cl.hortiscan.hortiscan_demo.model.dao.UsuarioDAO;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioDTO;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioRegistroDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {
  @Autowired
  private UsuarioDAO usuarioDAO;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private final String ROOT_DIRECTORY = "C:\\folderToUsers";

  @Override
  public UsuarioDTO saveUser(UsuarioRegistroDTO usuarioRegistro) {
    String username = usuarioRegistro.getUsername();
    String password = usuarioRegistro.getPassword();

    Usuario usuario = new Usuario();
    usuario.setUsername(username);
    usuario.setPassword(passwordEncoder.encode(password));

    Usuario usuarioGuardado = usuarioDAO.save(usuario);

    return new UsuarioDTO(usuarioGuardado.getIdUsuario(), usuarioGuardado.getUsername(), null);
  }

  @Override
  public UsuarioDTO findByUsername(String username) {
    Usuario usuario = usuarioDAO.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    return new UsuarioDTO(usuario.getIdUsuario(), usuario.getUsername(), null);
  }

  @Override
  public Integer findIdByUsername(String username) {
    Usuario usuario = usuarioDAO.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    return usuario.getIdUsuario();
  }

  @Override
  public List<UsuarioDTO> listUsers() {
    return usuarioDAO.findAll()
            .stream()
            .map(usuario -> new UsuarioDTO(usuario.getIdUsuario(), usuario.getUsername(), null))
            .collect(Collectors.toList());
  }

  @Override
  public void validateOrCreateFolder(Integer idUsuario) {
    File rootDirectory = new File(ROOT_DIRECTORY);
    if (!rootDirectory.exists()) {
      System.out.println("El directorio raíz no existe: " + ROOT_DIRECTORY);
      rootDirectory.mkdir();
    }


    // Crea la ruta de la carpeta basada en el ID del usuario
    String userFolder = ROOT_DIRECTORY + File.separator + "usuario_" + idUsuario;

    // Objeto folder
    File folder = new File(userFolder);

    // Valida si la carpeta existe
    if (!folder.exists()) {
      boolean folderCreated = folder.mkdir();

      if(folderCreated) {
        System.out.println("Carpeta creada para el usuario con ID: " + idUsuario);
      } else {
        throw new RuntimeException("Error al crear carpeta para el usuario con ID: " + idUsuario);
      }
    } else {
      System.out.println("Carpeta ya existe para el usuario con ID: " + idUsuario);
    }
  }

  @Override
  public void createFolderUser(Integer idUsuario, String nameFolder) {
    String userFolder = ROOT_DIRECTORY + File.separator + "usuario_" + idUsuario;
    String newFolderPath = userFolder + File.separator + nameFolder;

    File newFolder = new File(newFolderPath);
    if(!newFolder.exists()) {
      boolean folderCreated = newFolder.mkdir();

      if(!folderCreated) {
        throw new RuntimeException("Error al crear la nueva carpeta: " + newFolderPath);
      }
    } else {
      System.out.println("Carpeta ya existe para el usuario con ID: " + idUsuario);
    }
  }

  @Override
  public String saveImage(Integer idUsuario, MultipartFile file, String nameFolder) {
    String userFolder = ROOT_DIRECTORY + File.separator + "usuario_" + idUsuario + File.separator + nameFolder;

    // Se asegura si la carpeta existe
    File folder = new File(userFolder);
    if(!folder.exists()) {
      folder.mkdirs();
    }

    // Guardar imagen
    String filePath = userFolder + File.separator + file.getOriginalFilename();
    File destiny = new File(filePath);
    try {
      file.transferTo(destiny);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return filePath;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario = usuarioDAO.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    return new User(usuario.getUsername(), usuario.getPassword(), new ArrayList<>());
  }
}
