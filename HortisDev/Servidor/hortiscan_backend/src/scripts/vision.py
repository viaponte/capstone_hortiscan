import os
import re
from google.cloud import vision
from docx import Document
from argparse import ArgumentParser

# Configurar credenciales de Google Vision
os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = 'src/scripts/decisive-window-440821-u1-0d038f28fa54.json'

# Crear cliente de Vision
client = vision.ImageAnnotatorClient()

def process_text(text):
    # Eliminar saltos de línea innecesarios y mantener el texto fluido
    text = re.sub(r'(?<![.!?])\n', ' ', text)  # Unir líneas que no terminan en signos de puntuación
    text = re.sub(r'\n+', '\n\n', text)  # Reemplazar saltos de línea múltiples con dos para separar párrafos
    return text.strip()

def extract_text_google_vision(image_path):
    try:
        # Leer la imagen y convertirla en bytes
        with open(image_path, 'rb') as image_file:
            content = image_file.read()

        # Crear la solicitud de OCR
        image = vision.Image(content=content)
        response = client.text_detection(image=image)
        annotations = response.text_annotations

        if response.error.message:
            raise Exception(f"Error en Google Vision API: {response.error.message}")

        # Retornar el texto más largo identificado
        if len(annotations) > 0:
            raw_text = annotations[0].description
            processed_text = process_text(raw_text)
            return processed_text
        else:
            return ""

    except Exception as e:
        print(f"Error al procesar la imagen con Google Vision OCR: {e}")
        return ""

def save_text_to_word(text, file_name):
    # Crear un nuevo documento de Word
    document = Document()

    # Agregar el texto al documento
    paragraphs = text.split("\n\n")
    for paragraph in paragraphs:
        document.add_paragraph(paragraph.strip())

    # Guardar el documento
    document.save(file_name)

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
        text_google_vision = extract_text_google_vision(image_path)
        if text_google_vision:
            print("Texto estructurado detectado.")
        else:
            raise ValueError("No se detectó texto en la imagen.")

        # Guardar el texto extraído en un archivo Word
        save_text_to_word(text_google_vision, output_path)
        print(f"Texto guardado en el archivo Word: {output_path}")

    except ValueError as e:
        print(e)
    except Exception as e:
        print(f"Error general: {e}")
