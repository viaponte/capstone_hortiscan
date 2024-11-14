package cl.hortiscan.hortiscan_demo.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import cl.hortiscan.hortiscan_demo.model.dao.ImagenDAO;
import cl.hortiscan.hortiscan_demo.model.dao.UsuarioDAO;
import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;

import cl.hortiscan.hortiscan_demo.model.dto.UsuarioDTO;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioRegistroDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Imagen;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;
import cl.hortiscan.hortiscan_demo.model.exception.UsernameExists;

@Service
public class UsuarioServiceImpl implements UsuarioService {
  @Autowired
  private UsuarioDAO usuarioDAO;

  @Autowired
  private ImagenDAO imagenDAO;

  @Autowired
  private CarpetaServiceImpl carpetaServiceImpl;

  @Autowired
  private PasswordEncoder passwordEncoder;




  private final String ROOT_DIRECTORY = "C:\\folderToUsers";

  @Override
  public UsuarioDTO saveUser(UsuarioRegistroDTO usuarioRegistro) {
    // Almacena en las variables el username y password
    String username = usuarioRegistro.getUsername();
    String password = usuarioRegistro.getPassword();

    // Valida si el username está registrado mediante su ID
    if (findIdByUsername(usuarioRegistro.getUsername()) > 0) {
      throw new UsernameExists("Usuario existente");
    }

    // Se crea objeto tipo usuario con username y contraseña encriptada
    Usuario usuario = new Usuario();
    usuario.setUsername(username);
    usuario.setPassword(passwordEncoder.encode(password));

    // Se crea objeto que registra el usuario
    Usuario usuarioGuardado = usuarioDAO.save(usuario);

    // Valida o crea carpeta por id del usuario
    validateOrCreateFolder(usuarioGuardado.getIdUsuario());

    return new UsuarioDTO(usuarioGuardado.getIdUsuario(), usuarioGuardado.getUsername(), null);
  }

  @Override
  public UsuarioDTO findByUsername(String username) {
    Usuario usuario = usuarioDAO.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    return new UsuarioDTO(usuario.getIdUsuario(), usuario.getUsername(), null);
  }

  @Override
  public Usuario findUsuarioById(Integer idUsuario) {
    return usuarioDAO.findById(idUsuario).orElse(null);
  }

  @Override
  public Integer findIdByUsername(String username) {
    try {
      Usuario usuario = usuarioDAO.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
      return usuario.getIdUsuario();
    } catch (RuntimeException e) {
      return 0;
    }
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
  public void createFolderUser(CarpetaDTO carpetaDTO) {

    // Crea la ruta de la carpeta del usuario basada en el ID
    String userFolder = ROOT_DIRECTORY + File.separator + "usuario_" + carpetaDTO.getIdUsuario();
    String newFolderPath = userFolder + File.separator + carpetaDTO.getNombreCarpeta();

    // Crea un nuevo objeto File basado en la ruta de la nueva carpeta
    File newFolder = new File(newFolderPath);

    if (!newFolder.exists()) {
      boolean folderCreated = newFolder.mkdir();

      if (!folderCreated) {
        throw new RuntimeException("Error al crear la nueva carpeta: " + newFolderPath);
      }

      // Si la carpeta se creó, guarda la información de la carpeta en el DTO
      carpetaDTO.setRutaCarpeta(newFolderPath);
      carpetaDTO.setFechaCreacionCarpeta(new Date());

      // Guarda datos de carpetaDTO en BD
      carpetaServiceImpl.saveCarpeta(carpetaDTO);

      System.out.println("Carpeta creada exitosamente: " + newFolderPath);
    } else {
      System.out.println("Carpeta ya existe para el usuario con ID: " + carpetaDTO.getIdUsuario());
    }
  }

  @Override
  public String saveImage(Integer idUsuario, MultipartFile file, String nameFolder) {
    String userFolder = ROOT_DIRECTORY + File.separator + "usuario_" + idUsuario + File.separator + nameFolder;

    // Se asegura si la carpeta existe
    File folder = new File(userFolder);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    // Guardar imagen en la ruta del sistema de archivos
    String filePath = userFolder + File.separator + file.getOriginalFilename();
    File destiny = new File(filePath);
    try {
      file.transferTo(destiny);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Ahora obtenemos la entidad Carpeta usando el método del servicio
    Carpeta carpeta = this.carpetaServiceImpl.getCarpetaIdByNombreAndUsuario(nameFolder, idUsuario);

    // Creación de la entidad Imagen y guardado en la base de datos
    Imagen imagen = new Imagen();
    imagen.setIdFormulario(null);  // Si no tienes un formulario asignado aún
    imagen.setIdCarpeta(carpeta);  // Usa la carpeta ya obtenida del servicio
    imagen.setRutaAlmacenamiento(filePath);  // Almacena la ruta del archivo
    imagen.setFechaCreacionImagen(new Date());  // Fecha actual

    // Guardar la imagen en la base de datos
    imagenDAO.save(imagen);

    return filePath;  // Retorna la ruta donde se guardó la imagen
  }
  
  @Override
  public String saveWordDocument(Integer idUsuario, MultipartFile file, String folderName) throws IOException {
    String userFolderPath = ROOT_DIRECTORY + File.separator + "usuario_" + idUsuario + File.separator + folderName;
    File folder = new File(userFolderPath);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    String filePath = userFolderPath + File.separator + file.getOriginalFilename();
    file.transferTo(new File(filePath)); // Guarda el archivo en la ruta especificada

    return filePath; // Retorna la ruta donde se guardó el archivo
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario = usuarioDAO.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    return new User(usuario.getUsername(), usuario.getPassword(), new ArrayList<>());
  }
}
