package cl.hortiscan.hortiscan_demo.negocio;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.CTLanguage;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.RFonts;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class WordToPdf2 {

  private static final int MAX_FONT_LOOKUP_ATTEMPTS = 2;
  private static final String DEFAULT_FONT = "Calibri";

  public void convertWordToPdf(InputStream wordInputStream, OutputStream pdfOutputStream) throws Exception {
    // Configurar Apache Xerces como SAXParserFactory y DocumentBuilderFactory
    System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
    System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");

    // Cargar el documento Word
    WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(wordInputStream);

    // Verificar si el documento tiene contenido antes de proceder
    if (wordMLPackage.getMainDocumentPart() == null || wordMLPackage.getMainDocumentPart().getContents() == null) {
      throw new IllegalArgumentException("El documento de Word está vacío o no tiene contenido válido.");
    }

    // Manejar problemas de JAXBException causados por números no enteros
    try {
      wordMLPackage.getMainDocumentPart().getContents();
    } catch (NumberFormatException e) {
      System.err.println("Error al analizar números en el documento: " + e.getMessage());
      throw new IllegalArgumentException("El documento contiene valores numéricos no válidos: " + e.getMessage(), e);
    } catch (Exception e) {
      System.err.println("Error general al procesar el contenido del documento: " + e.getMessage());
      throw new IllegalArgumentException("El documento contiene contenido no válido: " + e.getMessage(), e);
    }

    // Configurar el FontMapper para evitar problemas con las fuentes
    Mapper fontMapper = new IdentityPlusMapper();
    wordMLPackage.setFontMapper(fontMapper);

    // Intentar cargar todas las fuentes requeridas, ignorando las problemáticas
    try {
      PhysicalFonts.discoverPhysicalFonts(); // Descubrir las fuentes disponibles en el sistema
    } catch (Exception e) {
      System.err.println("Error descubriendo las fuentes del sistema: " + e.getMessage());
    }

    // Manejar las fuentes del documento
    Map<String, Integer> fontLookupAttempts = new HashMap<>();
    if (wordMLPackage.getMainDocumentPart().getFontTablePart() != null
        && wordMLPackage.getMainDocumentPart().getFontTablePart().getContents() != null) {
      wordMLPackage.getMainDocumentPart().getFontTablePart().getContents().getFont().forEach(font -> {
        String fontName = font.getName();
        int attempts = fontLookupAttempts.getOrDefault(fontName, 0);

        while (attempts < MAX_FONT_LOOKUP_ATTEMPTS) {
          if (PhysicalFonts.get(fontName) != null) {
            // Fuente encontrada
            break;
          } else {
            attempts++;
            fontLookupAttempts.put(fontName, attempts);
          }
        }

        if (attempts >= MAX_FONT_LOOKUP_ATTEMPTS) {
          // Si no se encontró la fuente después de cinco intentos, sustituir por fuente
          // por defecto
          System.out.println("No se encontró la fuente '" + fontName + "' después de " + attempts
              + " intentos. Usando fuente por defecto: " + DEFAULT_FONT);
          fontMapper.put(fontName, PhysicalFonts.get(DEFAULT_FONT));
        }
      });
    }

    // Manejar fuentes del tema, como MINOR_EAST_ASIA y otros
    ObjectFactory factory = new ObjectFactory();
    RFonts rFonts = factory.createRFonts();
    rFonts.setAscii(DEFAULT_FONT);
    rFonts.setHAnsi(DEFAULT_FONT);
    rFonts.setCs(DEFAULT_FONT);
    rFonts.setEastAsia(DEFAULT_FONT);
    try {
      if (wordMLPackage.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr() != null) {
        wordMLPackage.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr().setRFonts(rFonts);
      }
    } catch (NullPointerException e) {
      System.err.println("Error configurando las fuentes predeterminadas del documento: " + e.getMessage());
    }

    // Configurar el idioma del documento para evitar problemas con fuentes
    // regionales
    CTLanguage language = factory.createCTLanguage();
    language.setVal("en-US"); // Establecer un idioma predeterminado
    try {
      if (wordMLPackage.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr() != null) {
        wordMLPackage.getMainDocumentPart().getPropertyResolver().getDocumentDefaultRPr().setLang(language);
      }
    } catch (NullPointerException e) {
      System.err.println("Error configurando el idioma del documento: " + e.getMessage());
    }

    // Configurar FOSettings para la conversión a PDF
    FOSettings foSettings = Docx4J.createFOSettings();
    foSettings.setWmlPackage(wordMLPackage);
    foSettings.setApacheFopMime(FOSettings.MIME_PDF);

    // Ajustar tamaño de las regiones para evitar el desbordamiento en la conversión
    String foConfig = "<fop version=\"1.0\">" +
        "<renderers>" +
        "<renderer mime=\"application/pdf\">" +
        "<region-name name=\"region-after\" extent=\"7mm\"/>" +
        "</renderer>" +
        "</renderers>" +
        "</fop>";
    foSettings.setApacheFopConfiguration(foConfig);

    // Realizar la conversión de Word a PDF
    try {
      Docx4J.toFO(foSettings, pdfOutputStream, Docx4J.FLAG_EXPORT_PREFER_XSL);
    } catch (Exception e) {
      System.err.println("Error durante la conversión a PDF: " + e.getMessage());
      e.printStackTrace();
      throw new IllegalArgumentException("Error durante la conversión a PDF: " + e.getMessage(), e);
    }
  }
}