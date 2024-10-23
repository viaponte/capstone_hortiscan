package cl.hortiscan.hortiscan_demo.negocio;

import java.io.InputStream;
import java.io.OutputStream;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.CTLanguage;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.RFonts;
import org.springframework.stereotype.Service;

@Service
public class WordToPdf2 {

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

        // Descubrir las fuentes físicas disponibles
        PhysicalFonts.discoverPhysicalFonts();

        // Mapear cada fuente del documento a la fuente física correspondiente
        if (wordMLPackage.getMainDocumentPart().getFontTablePart() != null
                && wordMLPackage.getMainDocumentPart().getFontTablePart().getContents() != null) {
            wordMLPackage.getMainDocumentPart().getFontTablePart().getContents().getFont().forEach(font -> {
                String fontName = font.getName();
                if (PhysicalFonts.get(fontName) != null) {
                    fontMapper.put(fontName, PhysicalFonts.get(fontName));
                } else {
                    System.out.println("Fuente no encontrada: " + fontName + ". Usando fuente por defecto: " + DEFAULT_FONT);
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

        // Configurar el idioma del documento para evitar problemas con fuentes regionales
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

        // Configurar FOP para encontrar las fuentes y definir el layout
        String foConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<fop version=\"2.7\">"
                + "  <renderers>"
                + "    <renderer mime=\"application/pdf\">"
                + "      <fonts>"
                + "        <directory>C:\\Windows\\Fonts</directory>" // Ajusta la ruta según tu sistema
                + "        <auto-fonts/>" // Permite a FOP detectar automáticamente las fuentes
                + "      </fonts>"
                + "      <page-sequence master-reference=\"simple\">"
                + "        <layout-master-set>"
                + "          <simple-page-master master-name=\"simple\" page-height=\"29.7cm\" page-width=\"21cm\" margin-top=\"2cm\" margin-bottom=\"2cm\" margin-left=\"2.5cm\" margin-right=\"2.5cm\">"
                + "            <region-body/>"
                + "          </simple-page-master>"
                + "        </layout-master-set>"
                + "      </page-sequence>"
                + "    </renderer>"
                + "  </renderers>"
                + "</fop>";
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