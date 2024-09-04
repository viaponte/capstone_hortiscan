package cl.hortiscan.hortiscan_demo.model.service;


import cl.hortiscan.hortiscan_demo.model.dto.UsuarioDTO;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioRegistroDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UsuarioService extends UserDetailsService {
  UsuarioDTO saveUser(UsuarioRegistroDTO usuarioRegistro);
  UsuarioDTO findByUsername(String username);
  List<UsuarioDTO> listUsers();
}
