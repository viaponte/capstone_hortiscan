import cv2
import pytesseract
from spellchecker import SpellChecker
from docx import Document
import sys
import os

# Configuración de Tesseract en Windows (ajusta la ruta si es necesario)
pytesseract.pytesseract.tesseract_cmd = 'C:/Tesseract-OCR/tesseract.exe'
os.environ['TESSDATA_PREFIX'] = 'C:/Tesseract-OCR/tessdata'

def preprocess_image(image_path):
    img = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
    if img is None:
        raise ValueError(f"No se pudo cargar la imagen en la ruta especificada: {image_path}")
    
    img = cv2.medianBlur(img, 3)
    clahe = cv2.createCLAHE(clipLimit=3.0, tileGridSize=(8, 8))
    enhanced_img = clahe.apply(img)
    _, binary_img = cv2.threshold(enhanced_img, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)
    return binary_img

def extract_text(image_path):
    processed_image = preprocess_image(image_path)
    config = "--oem 3 --psm 3"
    text = pytesseract.image_to_string(processed_image, config=config, lang='spa')
    return text

def correct_text(text):
    spell = SpellChecker(language='es')
    lines = text.splitlines()
    corrected_lines = []
    for line in lines:
        words = line.split()
        corrected_words = [spell.correction(word) if spell.correction(word) else word for word in words]
        corrected_lines.append(" ".join([word for word in corrected_words if word is not None]))
    return "\n".join(corrected_lines)


def save_to_word(text, output_path):
    doc = Document()
    for line in text.splitlines():
        doc.add_paragraph(line)
    doc.save(output_path)

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Uso: python ocr_script.py <ruta_imagen> <ruta_salida_word>")
        sys.exit(1)
    
    image_path = sys.argv[1]
    output_path = sys.argv[2]
    
    try:
        extracted_text = extract_text(image_path)
        corrected_text = correct_text(extracted_text)
        save_to_word(corrected_text, output_path)
        print(f"Texto corregido guardado en el archivo Word: {output_path}")
    except ValueError as e:
        print(e)