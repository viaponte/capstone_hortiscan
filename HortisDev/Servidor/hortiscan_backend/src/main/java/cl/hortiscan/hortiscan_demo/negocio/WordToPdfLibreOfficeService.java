package cl.hortiscan.hortiscan_demo.negocio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

@Service
public class WordToPdfLibreOfficeService {

<<<<<<< HEAD
    /**
     * Convierte un documento DOCX a PDF utilizando LibreOffice en modo headless.
     *
     * @param wordInputStream  InputStream del documento DOCX.
     * @param pdfOutputStream  OutputStream donde se escribirá el PDF generado.
     * @throws Exception       Si ocurre un error durante la conversión.
     */
    public void convertWordToPdf(InputStream wordInputStream, OutputStream pdfOutputStream) throws Exception {
        // Crear archivos temporales para DOCX y PDF
        File tempDoc = File.createTempFile("document", ".docx");
        File tempPdf = File.createTempFile("document", ".pdf");

        try {
            // Escribir el InputStream a un archivo temporal DOCX
            try (FileOutputStream fos = new FileOutputStream(tempDoc)) {
                copyStream(wordInputStream, fos);
            }

            // Ruta completa al ejecutable soffice.exe
            String libreOfficePath = "C:\\Program Files\\LibreOffice\\program\\soffice.exe";

            // Verificar que el ejecutable existe
            File libreOfficeExe = new File(libreOfficePath);
            if (!libreOfficeExe.exists()) {
                throw new IOException("El ejecutable de LibreOffice no se encontró en: " + libreOfficePath);
            }

            // Construir el comando para LibreOffice
            ProcessBuilder pb = new ProcessBuilder(
                    libreOfficePath,
                    "--headless",
                    "--convert-to",
                    "pdf",
                    "--outdir",
                    tempPdf.getParent(),
                    tempDoc.getAbsolutePath()
            );

            // Configurar el entorno si es necesario (opcional)
            pb.redirectErrorStream(true); // Combina stdout y stderr

            // Iniciar el proceso
            Process process = pb.start();

            // Leer y registrar la salida del proceso
            try (InputStream processOutput = process.getInputStream()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(processOutput));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("LibreOffice: " + line); // Reemplaza con un logger si lo prefieres
                }
            }

            // Esperar a que el proceso termine con un timeout de 60 segundos
            boolean finished = process.waitFor(60, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy(); // Finalizar el proceso si excede el tiempo
                throw new RuntimeException("La conversión a PDF excedió el tiempo límite.");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new RuntimeException("Error al convertir el documento usando LibreOffice. Código de salida: " + exitCode);
            }

            // El archivo PDF se generará con el mismo nombre que el DOCX
            // pero con extensión .pdf en el mismo directorio
            String pdfFilePath = tempDoc.getParent() + File.separator + getFileNameWithoutExtension(tempDoc) + ".pdf";
            File generatedPdf = new File(pdfFilePath);

            if (!generatedPdf.exists()) {
                throw new RuntimeException("El archivo PDF no se generó correctamente.");
            }

            // Escribir el PDF generado al OutputStream
            try (FileInputStream fis = new FileInputStream(generatedPdf)) {
                copyStream(fis, pdfOutputStream);
            }

        } finally {
            // Eliminar los archivos temporales
            tempDoc.delete();
            tempPdf.delete();
        }
    }

    /**
     * Copia el contenido de un InputStream a un OutputStream.
     *
     * @param in  InputStream de origen.
     * @param out OutputStream de destino.
     * @throws IOException Si ocurre un error durante la copia.
     */
    private void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }

    /**
     * Obtiene el nombre del archivo sin su extensión.
     *
     * @param file Archivo del cual obtener el nombre.
     * @return Nombre del archivo sin extensión.
     */
    private String getFileNameWithoutExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot == -1) {
            return name;
        }
        return name.substring(0, lastDot);
    }
=======
  /**
   * Convierte un documento DOCX a PDF utilizando LibreOffice en modo headless.
   *
   * @param wordInputStream InputStream del documento DOCX.
   * @param pdfOutputStream OutputStream donde se escribirá el PDF generado.
   * @throws Exception Si ocurre un error durante la conversión.
   */
  public void convertWordToPdf(InputStream wordInputStream, OutputStream pdfOutputStream) throws Exception {
    // Crear archivos temporales para DOCX y PDF
    String tempDocName = "document_" + System.currentTimeMillis() + ".docx";
    File tempDoc = new File(System.getProperty("java.io.tmpdir"), tempDocName);

    String tempPdfName = "document_" + System.currentTimeMillis() + ".pdf";
    File tempPdf = new File(System.getProperty("java.io.tmpdir"), tempPdfName);

    try {
      // Escribir el InputStream a un archivo temporal DOCX
      try (FileOutputStream fos = new FileOutputStream(tempDoc)) {
        copyStream(wordInputStream, fos);
      }

      // Ruta completa al ejecutable soffice.exe
      String libreOfficePath = "C:\\Program Files\\LibreOffice\\program\\soffice.exe";

      // Verificar que el ejecutable existe
      File libreOfficeExe = new File(libreOfficePath);
      if (!libreOfficeExe.exists()) {
        throw new IOException("El ejecutable de LibreOffice no se encontró en: " + libreOfficePath);
      }

      // Construir el comando para LibreOffice
      ProcessBuilder pb = new ProcessBuilder(
          libreOfficePath,
          "--headless",
          "--nologo",
          "--nolockcheck",
          "--convert-to",
          "pdf",
          "--outdir",
          tempPdf.getParent(),
          tempDoc.getAbsolutePath());

      // Configurar el entorno si es necesario (opcional)
      pb.redirectErrorStream(true); // Combina stdout y stderr

      // Iniciar el proceso
      Process process = pb.start();

      // Leer y registrar la salida del proceso
      try (InputStream processOutput = process.getInputStream()) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(processOutput));
        String line;
        while ((line = reader.readLine()) != null) {
          System.out.println("LibreOffice: " + line); // Reemplaza con un logger si lo prefieres
        }
      }

      // Esperar a que el proceso termine con un timeout de 60 segundos
      boolean finished = process.waitFor(60, TimeUnit.SECONDS);
      if (!finished) {
        process.destroy(); // Finalizar el proceso si excede el tiempo
        throw new RuntimeException("La conversión a PDF excedió el tiempo límite.");
      }

      int exitCode = process.exitValue();
      if (exitCode != 0) {
        throw new RuntimeException(
            "Error al convertir el documento usando LibreOffice. Código de salida: " + exitCode);
      }

      // El archivo PDF se generará con el mismo nombre que el DOCX
      // pero con extensión .pdf en el mismo directorio
      String pdfFilePath = tempDoc.getParent() + File.separator + getFileNameWithoutExtension(tempDoc) + ".pdf";
      File generatedPdf = new File(pdfFilePath);

      if (!generatedPdf.exists()) {
        throw new RuntimeException("El archivo PDF no se generó correctamente.");
      }

      // Verificar que el PDF es más reciente que el DOCX
      if (generatedPdf.lastModified() < tempDoc.lastModified()) {
        throw new RuntimeException("El archivo PDF generado no es la versión más reciente.");
      }

      // Escribir el PDF generado al OutputStream
      try (FileInputStream fis = new FileInputStream(generatedPdf)) {
        copyStream(fis, pdfOutputStream);
      }

    } finally {
      // Eliminar los archivos temporales
      if (tempDoc.exists())
        tempDoc.delete();
      if (tempPdf.exists())
        tempPdf.delete();
    }
  }

  /**
   * Copia el contenido de un InputStream a un OutputStream.
   *
   * @param in  InputStream de origen.
   * @param out OutputStream de destino.
   * @throws IOException Si ocurre un error durante la copia.
   */
  private void copyStream(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = in.read(buffer)) != -1) {
      out.write(buffer, 0, bytesRead);
    }
  }

  /**
   * Obtiene el nombre del archivo sin su extensión.
   *
   * @param file Archivo del cual obtener el nombre.
   * @return Nombre del archivo sin extensión.
   */
  private String getFileNameWithoutExtension(File file) {
    String name = file.getName();
    int lastDot = name.lastIndexOf('.');
    if (lastDot == -1) {
      return name;
    }
    return name.substring(0, lastDot);
  }
>>>>>>> develop
}
