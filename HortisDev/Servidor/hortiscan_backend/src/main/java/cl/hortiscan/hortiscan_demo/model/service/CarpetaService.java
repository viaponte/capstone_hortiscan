package cl.hortiscan.hortiscan_demo.model.service;

import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;

import java.util.List;


public interface CarpetaService {
  Carpeta saveCarpeta(CarpetaDTO carpetaDTO);
  List<CarpetaDTO> findCarpetasByUsuario(Integer idUsuario);
  List<String> getContenidoCarpeta(Integer idUsuario, String nombreCarpeta);
}
