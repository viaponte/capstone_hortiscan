from argparse import ArgumentParser
import cv2
import pytesseract
from imutils import resize
from docx import Document
from docx.shared import Pt
from docx.oxml.ns import qn
from docx.oxml import OxmlElement
import os

# Ruta de Tesseract en Windows
pytesseract.pytesseract.tesseract_cmd = 'C:/Tesseract-OCR/tesseract.exe'
os.environ['TESSDATA_PREFIX'] = 'C:/Tesseract-OCR/tessdata'

# Función para preprocesar la imagen
def preprocess_image(image_path):
    image = cv2.imread(image_path)
    if image is None:
        raise ValueError(f"No se pudo cargar la imagen en la ruta especificada: {image_path}")
    
    # Redimensionar y convertir a escala de grises
    resized_image = resize(image, width=800)
    gray = cv2.cvtColor(resized_image, cv2.COLOR_BGR2GRAY)

    # Aplicar desenfoque para suavizar la imagen
    blurred = cv2.GaussianBlur(gray, (3, 3), 0)

    # Aplicar thresholding automático con Otsu
    thresholded = cv2.threshold(blurred, 0, 255, cv2.THRESH_BINARY_INV | cv2.THRESH_OTSU)[1]

    return thresholded

# Función para agrupar palabras en líneas y detectar sangrías
def group_text_by_lines(results, y_tolerance=20):
    lines = []
    current_line = []
    current_y = results['top'][0]
    previous_x = results['left'][0]

    for i in range(len(results['text'])):
        text = results['text'][i].strip()
        confidence = int(results['conf'][i])

        # Ignorar texto con baja confianza
        if confidence < 30 or not text:
            continue

        x = results['left'][i]
        y = results['top'][i]

        # Nueva línea si la diferencia en 'y' es mayor a y_tolerance
        if abs(y - current_y) > y_tolerance:
            if current_line:
                lines.append((current_line, previous_x))
            current_line = [text]
            current_y = y
            previous_x = x
        else:
            current_line.append(text)

    # Agregar la última línea
    if current_line:
        lines.append((current_line, previous_x))

    return lines

# Función para extraer y procesar el texto de la imagen
def extract_text(image_path):
    processed_image = preprocess_image(image_path)

    # Obtener datos detallados de Tesseract
    results = pytesseract.image_to_data(processed_image, output_type=pytesseract.Output.DICT)
    lines = group_text_by_lines(results, y_tolerance=20)
    
    return lines

# Función para guardar el texto en un archivo Word
def save_to_word(lines, output_path):
    doc = Document()
    doc.add_heading("Texto Extraído", level=1)

    for line, indent_x in lines:
        text = ' '.join(line)
        paragraph = doc.add_paragraph(text)
        paragraph_format = paragraph.paragraph_format
        paragraph_format.space_after = Pt(0)
        paragraph_format.line_spacing = 1

        if indent_x > 70:
            paragraph_format.left_indent = Pt((indent_x - 70) / 10)

        if text.startswith('-') or text.startswith('*') or (text[0].isdigit() and text[1] == '.'):
            p = paragraph._element
            pPr = p.get_or_add_pPr()
            numPr = OxmlElement('w:numPr')
            ilvl = OxmlElement('w:ilvl')
            ilvl.set(qn('w:val'), "0")
            numId = OxmlElement('w:numId')
            numId.set(qn('w:val'), "1")
            numPr.append(ilvl)
            numPr.append(numId)
            pPr.append(numPr)

    doc.save(output_path)

# Programa principal
if __name__ == "__main__":
    # Definir argumentos de entrada del programa
    argument_parser = ArgumentParser()
    argument_parser.add_argument('-i', '--image', type=str, required=True, help='Ruta a la imagen de entrada.')
    argument_parser.add_argument('-o', '--output', type=str, required=True, help='Ruta del archivo de salida en formato Word.')
    arguments = vars(argument_parser.parse_args())

    image_path = arguments['image']
    output_path = arguments['output']

    try:
        # Extraer texto de la imagen
        lines = extract_text(image_path)
        print("Texto estructurado detectado.")

        # Guardar el texto extraído en un archivo Word
        save_to_word(lines, output_path)
        print(f"Texto guardado en el archivo Word: {output_path}")

    except ValueError as e:
        print(e)

