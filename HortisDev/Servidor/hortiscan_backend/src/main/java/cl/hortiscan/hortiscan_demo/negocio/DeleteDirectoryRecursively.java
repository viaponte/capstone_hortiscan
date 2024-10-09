package cl.hortiscan.hortiscan_demo.negocio;

import java.io.File;

import org.springframework.stereotype.Service;

@Service
public class DeleteDirectoryRecursively {
  // Método recursivo para eliminar directorios y archivos
  public void deleteDirectoryRecursively(File file) {
    File[] contents = file.listFiles();
    if (contents != null) {
      for (File f : contents) {
        if (f.isDirectory()) {
          deleteDirectoryRecursively(f);
        } else {
          f.delete(); // Eliminar archivo
        }
      }
    }
    file.delete(); // Finalmente, eliminar el directorio
  }
}
