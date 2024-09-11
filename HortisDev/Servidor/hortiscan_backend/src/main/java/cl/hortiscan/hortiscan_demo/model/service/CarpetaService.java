package cl.hortiscan.hortiscan_demo.model.service;

import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;


public interface CarpetaService {
  Carpeta saveCarpeta(CarpetaDTO carpetaDTO);
}
