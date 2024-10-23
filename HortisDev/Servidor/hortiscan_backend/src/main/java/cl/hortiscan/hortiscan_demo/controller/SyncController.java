package cl.hortiscan.hortiscan_demo.controller;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Formulario;
import cl.hortiscan.hortiscan_demo.model.entity.Imagen;
import cl.hortiscan.hortiscan_demo.model.service.CarpetaService;
import cl.hortiscan.hortiscan_demo.model.service.FormularioService;
import cl.hortiscan.hortiscan_demo.model.service.ImagenService;

@RestController
public class SyncController {
  @Autowired
  private CarpetaService carpetaService;

  @Autowired
  private ImagenService imagenService;

  @Autowired
  private FormularioService formularioService;

  // Directorio de carpetas locales
  private final String ROOT_DIRECTORY = "C:\\folderToUsers";

  @PostMapping("/sync-carpetas-imagenes")
  public ResponseEntity<String> syncCarpetasEImagenes() {
    try {
      // Sincronizar carpetas y sus imágenes
      List<Carpeta> carpetasBD = carpetaService.getAllCarpetas();

      for (Carpeta carpeta : carpetasBD) {
        String carpetaPath = ROOT_DIRECTORY + File.separator + "usuario_" + carpeta.getIdUsuario().getIdUsuario()
            + File.separator + carpeta.getNombreCarpeta();
        File carpetaLocal = new File(carpetaPath);

        if (!carpetaLocal.exists()) {
          // Eliminar la carpeta de la BD si no existe en el sistema de archivos
          carpetaService.deleteCarpeta(carpeta.getIdCarpeta());
          System.out.println("Eliminada carpeta en BD: " + carpeta.getNombreCarpeta());
        } else {
          System.out.println("Carpeta existe");

          // Sincronizar imágenes asociadas a la carpeta
          List<Imagen> imagenesBD = imagenService.getImagenesByCarpeta(carpeta.getIdCarpeta());
          for (Imagen imagen : imagenesBD) {
            File imagenLocal = new File(imagen.getRutaAlmacenamiento());
            if (!imagenLocal.exists()) {
              // Si la imagen no existe, eliminarla de la BD
              imagenService.deleteImage(imagen.getIdImagen());
              System.out.println("Eliminada imagen de BD: " + imagen.getRutaAlmacenamiento());
            }
          }

          // Verificar si hay imágenes locales que no están en la BD
          File[] archivosLocal = carpetaLocal
              .listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg"));
          if (archivosLocal != null) {
            for (File archivoLocal : archivosLocal) {
              String rutaArchivoBD = carpetaPath + File.separator + archivoLocal.getName();
              Imagen imagenBD = imagenService.findByRutaAlmacenamiento(rutaArchivoBD);

              if (imagenBD == null) {
                // Eliminar imagen del sistema si no está en la BD
                archivoLocal.delete();
                System.out.println("Imagen eliminada del sistema: " + archivoLocal.getName());
              } else {
                System.out.println("Imagen existente en BD: " + archivoLocal.getName());
              }
            }
          }
        }
      }

      // Ahora sincronizar formularios y sus imágenes asociadas
      List<Formulario> formulariosBD = formularioService.getAllFormularios();
      for (Formulario formulario : formulariosBD) {
        String formularioPath = ROOT_DIRECTORY + File.separator + "usuario_" + formulario.getIdUsuario().getIdUsuario()
            + File.separator + "formularios" + File.separator + formulario.getNombreFormulario();
        File formularioLocal = new File(formularioPath);

        if (!formularioLocal.exists()) {
          // Si el formulario no existe en el sistema, eliminarlo de la BD
          formularioService.deleteFormulario(formulario.getIdFormulario());
          System.out.println("Eliminado formulario de BD: " + formulario.getNombreFormulario());
        } else {
          // Sincronizar las imágenes asociadas al formulario
          List<Imagen> imagenesBD = formulario.getImagenes();
          for (Imagen imagen : imagenesBD) {
            File imagenLocal = new File(imagen.getRutaAlmacenamiento());
            if (!imagenLocal.exists()) {
              // Si la imagen no existe, eliminarla de la BD
              imagenService.deleteImage(imagen.getIdImagen());
              System.out.println("Eliminada imagen de BD asociada al formulario: " + imagen.getRutaAlmacenamiento());
            }
          }

          // Verificar si hay imágenes locales que no están en la BD
          File[] archivosLocal = formularioLocal
              .listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg"));
          if (archivosLocal != null) {
            for (File archivoLocal : archivosLocal) {
              String rutaArchivoBD = formularioPath + File.separator + archivoLocal.getName();
              Imagen imagenBD = imagenService.findByRutaAlmacenamiento(rutaArchivoBD);

              if (imagenBD == null) {
                // Eliminar imagen del sistema si no está en la BD
                archivoLocal.delete();
                System.out.println("Imagen eliminada del sistema: " + archivoLocal.getName());
              }
            }
          }
        }
      }

      return ResponseEntity.ok("Sincronización de carpetas, imágenes y formularios completada con éxito");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error durante la sincronización");
    }
  }

}