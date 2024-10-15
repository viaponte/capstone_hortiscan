// package cl.hortiscan.hortiscan_demo.negocio;

// import java.io.IOException;
// import java.io.InputStream;
// import java.nio.file.Files;
// import java.nio.file.Path;

// import org.apache.commons.io.output.ByteArrayOutputStream;
// import org.apache.poi.xwpf.usermodel.IBodyElement;
// import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
// import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
// import org.apache.poi.xwpf.usermodel.XWPFDocument;
// import org.apache.poi.xwpf.usermodel.XWPFFooter;
// import org.apache.poi.xwpf.usermodel.XWPFHeader;
// import org.apache.poi.xwpf.usermodel.XWPFParagraph;
// import org.apache.poi.xwpf.usermodel.XWPFPictureData;
// import org.apache.poi.xwpf.usermodel.XWPFRun;
// import org.apache.poi.xwpf.usermodel.XWPFTable;
// import org.apache.poi.xwpf.usermodel.XWPFTableCell;
// import org.apache.poi.xwpf.usermodel.XWPFTableRow;
// import org.springframework.stereotype.Service;

// import com.itextpdf.text.BaseColor;
// import com.itextpdf.text.Chunk;
// import com.itextpdf.text.Document;
// import com.itextpdf.text.DocumentException;
// import com.itextpdf.text.Element;
// import com.itextpdf.text.Font;
// import com.itextpdf.text.Image;
// import com.itextpdf.text.Paragraph;
// import com.itextpdf.text.Phrase;
// import com.itextpdf.text.html.WebColors;
// import com.itextpdf.text.pdf.ColumnText;
// import com.itextpdf.text.pdf.PdfContentByte;
// import com.itextpdf.text.pdf.PdfPCell;
// import com.itextpdf.text.pdf.PdfPTable;
// import com.itextpdf.text.pdf.PdfWriter;

// @Service
// public class WordToPdf {

//   public void convertirWordAPdf(Path wordFilePath, ByteArrayOutputStream pdfOutputStream) throws Exception {
//     try (InputStream wordInputStream = Files.newInputStream(wordFilePath);
//         XWPFDocument wordDocument = new XWPFDocument(wordInputStream)) {

//       Document pdfDocument = new Document();
//       PdfWriter writer = PdfWriter.getInstance(pdfDocument, pdfOutputStream);
//       pdfDocument.open();

//       // Agregar encabezados y pies de página
//       agregarEncabezadosYPiesDePagina(writer, wordDocument);
//       // Agregar imágenes desde el documento Word
//       agregarImagenes(wordDocument, pdfDocument);

//       // Iterar sobre los elementos del documento Word
//       for (IBodyElement element : wordDocument.getBodyElements()) {
//         if (element instanceof XWPFParagraph) {
//           agregarParrafoConFormato((XWPFParagraph) element, pdfDocument);
//         } else if (element instanceof XWPFTable) {
//           agregarTabla((XWPFTable) element, pdfDocument);
//         }
//       }

//       pdfDocument.close();
//     }
//   }

//   // Método para agregar párrafos con formato mejorado
//   private void agregarParrafoConFormato(XWPFParagraph paragraph, Document pdfDocument) throws DocumentException {
//     Paragraph pdfParagraph = new Paragraph();
//     for (XWPFRun run : paragraph.getRuns()) {
//       Font font = new Font();
//       if (run.isBold())
//         font.setStyle(Font.BOLD);
//       if (run.isItalic())
//         font.setStyle(Font.ITALIC);
//       if (run.isStrike())
//         font.setStyle(Font.STRIKETHRU);
//       if (run.getUnderline() != UnderlinePatterns.NONE)
//         font.setStyle(Font.UNDERLINE);

//       // Color de la fuente
//       if (run.getColor() != null) {
//         BaseColor color = WebColors.getRGBColor(run.getColor());
//         font.setColor(color);
//       }
//       // Tamaño de fuente
//       if (run.getFontSize() > 0) {
//         font.setSize(run.getFontSize());
//       }

//       // Añadir texto con el formato aplicado
//       String text = run.getText(0);
//       if (text != null) {
//         Chunk chunk = new Chunk(text, font);
//         pdfParagraph.add(chunk);
//       }
//     }

//     // Alineación del párrafo
//     pdfParagraph.setAlignment(getAlignment(paragraph.getAlignment()));
//     // Añadir el párrafo al documento PDF
//     pdfDocument.add(pdfParagraph);
//   }

//   // Método para agregar tablas mejorado para mantener el estilo
//   private void agregarTabla(XWPFTable table, Document pdfDocument) throws DocumentException {
//     int numColumns = table.getRow(0).getTableCells().size();
//     PdfPTable pdfTable = new PdfPTable(numColumns);

//     for (XWPFTableRow row : table.getRows()) {
//       for (XWPFTableCell cell : row.getTableCells()) {
//         Phrase cellPhrase = new Phrase(cell.getText());
//         PdfPCell pdfCell = new PdfPCell(cellPhrase);
//         // Aplicar estilos de celdas, como bordes y alineación
//         pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//         pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//         pdfTable.addCell(pdfCell);
//       }
//     }

//     pdfDocument.add(pdfTable);
//   }

//   // Método para agregar encabezados y pies de página mejorado
//   private void agregarEncabezadosYPiesDePagina(PdfWriter writer, XWPFDocument document) throws DocumentException {
//     PdfContentByte cb = writer.getDirectContent();

//     for (XWPFHeader header : document.getHeaderList()) {
//       if (header.getText() != null) {
//         Phrase headerPhrase = new Phrase(header.getText(), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
//         ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, headerPhrase, 300, 800, 0);
//       }
//     }

//     for (XWPFFooter footer : document.getFooterList()) {
//       if (footer.getText() != null) {
//         Phrase footerPhrase = new Phrase(footer.getText(),
//             new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
//         ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footerPhrase, 300, 50, 0);
//       }
//     }
//   }

//   // Método para agregar imágenes mejorado
//   private void agregarImagenes(XWPFDocument document, Document pdfDocument) throws IOException, DocumentException {
//     for (XWPFPictureData pictureData : document.getAllPictures()) {
//       byte[] imageBytes = pictureData.getData();
//       Image img = Image.getInstance(imageBytes);
//       img.setAlignment(Image.ALIGN_CENTER);
//       img.scaleToFit(500, 500);
//       pdfDocument.add(img);
//     }
//   }

//   // Método para convertir alineaciones de Apache POI a iTextPDF
//   private int getAlignment(ParagraphAlignment alignment) {
//     if (alignment == null) {
//       return Element.ALIGN_LEFT;
//     }
//     switch (alignment) {
//       case CENTER:
//         return Element.ALIGN_CENTER;
//       case RIGHT:
//         return Element.ALIGN_RIGHT;
//       case BOTH:
//         return Element.ALIGN_JUSTIFIED;
//       case LEFT:
//       default:
//         return Element.ALIGN_LEFT;
//     }
//   }
// }