package cl.hortiscan.hortiscan_demo.model.service;


import java.io.IOException;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioDTO;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioRegistroDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;

public interface UsuarioService extends UserDetailsService {
  UsuarioDTO saveUser(UsuarioRegistroDTO usuarioRegistro);
  UsuarioDTO findByUsername(String username);
  Usuario findUsuarioById(Integer idUsuario);
  Integer findIdByUsername(String username);
  List<UsuarioDTO> listUsers();
  void validateOrCreateFolder(Integer idUsuario);
  void createFolderUser(CarpetaDTO carpetaDTO);
  String saveImage(Integer idUsuario, MultipartFile file, String nameFolder);
  String saveWordDocument(Integer idUsuario, MultipartFile file, String folderName) throws IOException;

  List<Usuario> getAllUsuarios();
}