package cl.hortiscan.hortiscan_demo.model.service;

import java.util.List;

import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;


public interface CarpetaService {
  Carpeta saveCarpeta(CarpetaDTO carpetaDTO);
  List<CarpetaDTO> findCarpetasByUsuario(Integer idUsuario);
  List<String> getContenidoCarpeta(Integer idUsuario, String nombreCarpeta);
  Carpeta getCarpetaIdByNombreAndUsuario(String nombreCarpeta, Integer idUsuario);
  List<Carpeta> getAllCarpetas();
  void deleteCarpeta(Integer idCarpeta);
  CarpetaDTO findByNombreCarpeta(String nombreCarpeta);
  Carpeta getCarpetaById(Integer idCarpeta);
}
