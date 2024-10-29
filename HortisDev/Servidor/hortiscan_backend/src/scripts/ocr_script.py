from argparse import ArgumentParser
import cv2
import numpy as np
import pytesseract
from docx import Document
import os

# Ruta de tesseract en windows
pytesseract.pytesseract.tesseract_cmd = 'C:/Tesseract-OCR/tesseract.exe'
os.environ['TESSDATA_PREFIX'] = 'C:/Tesseract-OCR/tessdata'

# Función para preprocesar la imagen
def preprocess_image(image_path):
    image = cv2.imread(image_path)
    if image is None:
        raise ValueError(f"No se pudo cargar la imagen en la ruta especificada: {image_path}")
    
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # Aplicar desenfoque para suavizar la imagen
    blurred = cv2.GaussianBlur(gray, (3, 3), 0)

    # Aplicar thresholding automático con Otsu
    thresholded = cv2.threshold(blurred, 0, 255, cv2.THRESH_BINARY_INV | cv2.THRESH_OTSU)[1]

    return thresholded

# Función para extraer el texto de la imagen
def extract_text(image_path):
    processed_image = preprocess_image(image_path)

    # Extraer texto utilizando Tesseract con algunas opciones configuradas
    options = '-l spa --oem 3 --psm 6'
    text = pytesseract.image_to_string(processed_image, config=options, lang='spa')
    
    return text

# Función para guardar el texto en un archivo Word
def save_to_word(text, output_path):
    doc = Document()
    for line in text.splitlines():
        doc.add_paragraph(line)
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
        extracted_text = extract_text(image_path)
        print("Texto detectado:", extracted_text)

        # Guardar el texto extraído en un archivo Word
        save_to_word(extracted_text, output_path)
        print(f"Texto guardado en el archivo Word: {output_path}")

    except ValueError as e:
        print(e)
