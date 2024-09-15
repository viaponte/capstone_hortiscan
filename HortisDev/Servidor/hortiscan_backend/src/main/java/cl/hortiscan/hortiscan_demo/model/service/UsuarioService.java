package cl.hortiscan.hortiscan_demo.model.service;


import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioDTO;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioRegistroDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UsuarioService extends UserDetailsService {
  UsuarioDTO saveUser(UsuarioRegistroDTO usuarioRegistro);
  UsuarioDTO findByUsername(String username);
  Usuario findUsuarioById(Integer idUsuario);
  Integer findIdByUsername(String username);
  List<UsuarioDTO> listUsers();
  void validateOrCreateFolder(Integer idUsuario);
  void createFolderUser(CarpetaDTO carpetaDTO);
  String saveImage(Integer idUsuario, MultipartFile file, String nameFolder);
}
