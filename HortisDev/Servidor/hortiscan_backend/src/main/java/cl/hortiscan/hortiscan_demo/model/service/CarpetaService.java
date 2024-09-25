package cl.hortiscan.hortiscan_demo.model.service;

import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Usuario;

import java.util.List;
import java.util.Optional;


public interface CarpetaService {
  Carpeta saveCarpeta(CarpetaDTO carpetaDTO);
  List<CarpetaDTO> findCarpetasByUsuario(Integer idUsuario);
  List<String> getContenidoCarpeta(Integer idUsuario, String nombreCarpeta);
  Carpeta getCarpetaIdByNombreAndUsuario(String nombreCarpeta, Integer idUsuario);
}
