package cl.hortiscan.hortiscan_demo.controller;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.hortiscan.hortiscan_demo.model.dto.CarpetaDTO;
import cl.hortiscan.hortiscan_demo.model.entity.Carpeta;
import cl.hortiscan.hortiscan_demo.model.entity.Imagen;
import cl.hortiscan.hortiscan_demo.model.service.CarpetaService;
import cl.hortiscan.hortiscan_demo.model.service.ImagenService;

@RestController
public class SyncController {
  @Autowired
  private CarpetaService carpetaService;

  @Autowired
  private ImagenService imagenService;

  // Directorio de carpetas locales
  private final String ROOT_DIRECTORY = "C:\\folderToUsers";

  @PostMapping("/sync-carpetas-imagenes")
  public ResponseEntity<String> syncCarpetasEImagenes() {
    try {
      // Obtener todas las carpetas registradas en la BD
      List<Carpeta> carpetasBD = carpetaService.getAllCarpetas();

      // Verificar la existencia de cada carpeta en el sistema de archivos
      for (Carpeta carpeta : carpetasBD) {
        String carpetaPath = ROOT_DIRECTORY + File.separator + "usuario_" + carpeta.getIdUsuario().getIdUsuario()
            + File.separator + carpeta.getNombreCarpeta();
        File carpetaLocal = new File(carpetaPath);

        // Sincronizar carpetas
        if (!carpetaLocal.exists()) {
          // Si la carpeta no existe en el sistema de archivos, eliminarla de la BD
          carpetaService.deleteCarpeta(carpeta.getIdCarpeta());
          System.out
              .println("Eliminada carpeta en BD, no existe en el sistema de archivos: " + carpeta.getNombreCarpeta());
        } else {
          System.out.println("Carpeta existe");

          // Si la carpeta existe, sincronizar las imágenes dentro de esa carpeta
          if (carpetaLocal.isDirectory()) {
            // Obtener imágenes asociadas a la carpeta en la BD
            List<Imagen> imagenesBD = imagenService.getImagenesByCarpeta(carpeta.getIdCarpeta());

            // Verificar la existencia de cada imagen en el sistema de archivos
            for (Imagen imagen : imagenesBD) {
              String rutaImagen = imagen.getRutaAlmacenamiento();
              File imagenLocal = new File(rutaImagen);

              if (!imagenLocal.exists()) {
                // Si la imagen no existe en el sistema de archivos, eliminarla de la BD
                imagenService.deleteImage(imagen.getIdImagen());
                System.out.println("Eliminada imagen en BD, no existe en el sistema de archivos: " + rutaImagen);
              }
            }

            // Verificar si las imágenes locales no están en la BD
            File[] archivosLocal = carpetaLocal.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg"));
            if (archivosLocal != null) {
              for (File archivoLocal : archivosLocal) {
                String nombreArchivo = archivoLocal.getName();
                String rutaArchivoBD = carpetaPath + File.separator + nombreArchivo;

                // Buscar en la BD si existe la imagen
                Imagen imagenBD = imagenService.findByRutaAlmacenamiento(rutaArchivoBD);
                if (imagenBD == null) {
                  // Si la imagen local no está en la BD, se elimina del sistema
                  archivoLocal.delete();
                  System.out.println("Se eliminó imagen local, no está en BD: " + nombreArchivo);
                } else {
                  System.out.println("Imagen local existente en BD: " + nombreArchivo);
                }
              }
            }
          }
        }
      }

      // Ahora, verificar las carpetas locales que no estén en la BD
      File rootDir = new File(ROOT_DIRECTORY);
      for (File userFolder : rootDir.listFiles()) {
        if (userFolder.isDirectory()) {
          File[] carpetasUsuario = userFolder.listFiles();
          for (File carpetaLocal : carpetasUsuario) {
            // Verificar si la carpeta local está en la BD
            String carpetaNombre = carpetaLocal.getName();
            CarpetaDTO carpetaBD = carpetaService.findByNombreCarpeta(carpetaNombre);

            if (carpetaBD == null) {
              // Si la carpeta local no está en la BD, eliminarla del sistema de archivos
              carpetaLocal.delete();
              System.out.println("Eliminada carpeta local, no está en la BD: " + carpetaNombre);
            }
          }
        }
      }

      return ResponseEntity.ok("Sincronización de carpetas e imágenes completada con éxito");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error durante la sincronización de carpetas e imágenes");
    }
  }

}