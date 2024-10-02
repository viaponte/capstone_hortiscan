package cl.hortiscan.hortiscan_demo.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.hortiscan.hortiscan_demo.model.dao.ImagenDAO;
import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Imagen;

@Service
public class ImagenServiceImpl implements ImagenService {
  @Autowired
  private ImagenDAO imagenDAO;

  @Autowired
  private CarpetaService carpetaService;

  @Override
  public List<Imagen> getAllImages() {
    return this.imagenDAO.findAll();
  }

  @Override
    public List<Imagen> getImagenesByCarpeta(Integer idCarpeta) {
      Carpeta carpeta = this.carpetaService.getCarpetaById(idCarpeta);

      if(carpeta == null) {
        return null;
      } else {
        return this.imagenDAO.findByIdCarpeta(carpeta);
      }
    }

    @Override
    public void deleteImage(Integer idImagen) {
        this.imagenDAO.deleteById(idImagen);
    }

    @Override
    public Imagen findByRutaAlmacenamiento(String rutaAchivo) {
      return imagenDAO.findByRutaAlmacenamiento(rutaAchivo).orElse(null);
    }

}
