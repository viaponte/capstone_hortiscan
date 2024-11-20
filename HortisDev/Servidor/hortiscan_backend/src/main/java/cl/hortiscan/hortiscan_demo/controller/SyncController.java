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
      // Obtener todas las carpetas y formularios de la BD
      List<Carpeta> carpetasBD = carpetaService.getAllCarpetas();
      List<Formulario> formulariosBD = formularioService.getAllFormularios();

      for (Carpeta carpeta : carpetasBD) {
        String carpetaPath = ROOT_DIRECTORY + File.separator + "usuario_" + carpeta.getIdUsuario().getIdUsuario()
            + File.separator + carpeta.getNombreCarpeta();
        File carpetaLocal = new File(carpetaPath);

        if (!carpetaLocal.exists()) {
          // Regla: Si la carpeta no existe en local, eliminarla de la BD junto con su
          // contenido
          carpetaService.deleteCarpeta(carpeta.getIdCarpeta());
          System.out.println("Eliminada carpeta en BD y su contenido: " + carpeta.getNombreCarpeta());
          continue;
        }

        System.out.println("Carpeta existe: " + carpeta.getNombreCarpeta());

        // Regla: Sincronizar imágenes asociadas a la carpeta
        List<Imagen> imagenesBD = imagenService.getImagenesByCarpeta(carpeta.getIdCarpeta());
        for (Imagen imagen : imagenesBD) {
          File imagenLocal = new File(imagen.getRutaAlmacenamiento());
          if (imagenLocal.exists()) {
            // Imagen en local y BD, no pasa nada
            System.out.println("Imagen existente en local y BD: " + imagen.getRutaAlmacenamiento());
          } else {
            // Imagen en BD pero no en local, eliminar el registro de la BD
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
              // Imagen en local pero no en BD, eliminar del sistema local
              archivoLocal.delete();
              System.out.println("Imagen eliminada del sistema: " + archivoLocal.getName());
            }
          }
        }

        // Regla: Sincronizar formularios dentro de la carpeta
        for (Formulario formulario : formulariosBD) {
          // Verificar si el formulario pertenece al usuario de la carpeta
          if (!formulario.getIdUsuario().getIdUsuario().equals(carpeta.getIdUsuario().getIdUsuario())) {
            continue;
          }

          // Construir la ruta completa del formulario dentro de la carpeta
          String formularioPath = carpetaPath + File.separator + formulario.getNombreFormulario();
          File formularioLocal = new File(formularioPath);

          if (formularioLocal.exists()) {
            // Formulario en local y BD, no pasa nada
            System.out.println("Formulario existente en local y BD: " + formulario.getNombreFormulario());
          } else {
            // Formulario en BD pero no en local, eliminar el registro de la BD
            formularioService.deleteFormulario(formulario.getIdFormulario());
            System.out.println("Formulario eliminado de BD: " + formulario.getNombreFormulario());
          }
        }

        // Verificar formularios locales que no están en la BD
        File[] formulariosLocales = carpetaLocal.listFiles((dir, name) -> name.toLowerCase().endsWith(".docx"));
        if (formulariosLocales != null) {
          for (File formularioLocal : formulariosLocales) {
            String rutaFormularioBD = carpetaPath + File.separator + formularioLocal.getName();
            boolean formularioExisteEnBD = formulariosBD.stream()
                .anyMatch(formulario -> formulario.getNombreFormulario().equals(formularioLocal.getName())
                    && formulario.getIdUsuario().getIdUsuario().equals(carpeta.getIdUsuario().getIdUsuario()));

            if (!formularioExisteEnBD) {
              // Formulario en local pero no en BD, eliminar del sistema local
              formularioLocal.delete();
              System.out.println("Formulario eliminado del sistema local: " + formularioLocal.getName());
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