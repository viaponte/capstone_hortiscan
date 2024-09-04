package cl.hortiscan.hortiscan_demo.model.service;

import cl.hortiscan.hortiscan_demo.model.dao.UsuarioDAO;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioDTO;
import cl.hortiscan.hortiscan_demo.model.dto.UsuarioRegistroDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {
  @Autowired
  private UsuarioDAO usuarioDAO;

  @Autowired
  private PasswordEncoder passwordEncoder;

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
  public List<UsuarioDTO> listUsers() {
    return usuarioDAO.findAll()
            .stream()
            .map(usuario -> new UsuarioDTO(usuario.getIdUsuario(), usuario.getUsername(), null))
            .collect(Collectors.toList());
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario usuario = usuarioDAO.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    return new User(usuario.getUsername(), usuario.getPassword(), new ArrayList<>());
  }
}
