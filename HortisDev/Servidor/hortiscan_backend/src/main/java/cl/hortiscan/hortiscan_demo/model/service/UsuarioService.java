package cl.hortiscan.hortiscan_demo.model.service;


import cl.hortiscan.hortiscan_demo.model.dto.UsuarioDTO;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioRegistroDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UsuarioService extends UserDetailsService {
  UsuarioDTO saveUser(UsuarioRegistroDTO usuarioRegistro);
  UsuarioDTO findByUsername(String username);
  Integer findIdByUsername(String username);
  List<UsuarioDTO> listUsers();
  void validateOrCreateFolder(Integer idUsuario);
  void createFolderUser(Integer idUsuario, String nameFolder);
  String saveImage(Integer idUsuario, MultipartFile file, String nameFolder);
}
