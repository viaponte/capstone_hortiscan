import torch
import torch.nn as nn
import cv2
import pytesseract
from imutils import resize
from docx import Document
import os
import easyocr
import re
import numpy as np
from modules.model import Model
from collections import OrderedDict
from paddleocr import PaddleOCR

# Configuración de Tesseract
pytesseract.pytesseract.tesseract_cmd = 'C:/Tesseract-OCR/tesseract.exe'  # Actualiza la ruta si es necesario
os.environ['TESSDATA_PREFIX'] = 'C:/Tesseract-OCR/tessdata'  # Actualiza la ruta si es necesario

# Función para preprocesar la imagen con umbral adaptativo
# Añadido: Preprocesamiento mejorado y opciones de filtrado

def preprocess_image(image_path):
    image = cv2.imread(image_path)
    if image is None:
        raise ValueError(f"No se pudo cargar la imagen: {image_path}")
    
    resized_image = resize(image, width=800)
    gray = cv2.cvtColor(resized_image, cv2.COLOR_BGR2GRAY)

    # Aplicar filtro Gaussiano para reducir ruido
    blurred = cv2.GaussianBlur(gray, (5, 5), 0)

    # Ajuste de contraste
    contrasted = cv2.convertScaleAbs(blurred, alpha=1.5, beta=20)

    # Prueba con diferentes métodos de umbralado
    _, binary_thresh = cv2.threshold(contrasted, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)
    adaptive_thresh = cv2.adaptiveThreshold(contrasted, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                            cv2.THRESH_BINARY, 15, 8)

    # Aplicar erosión y dilatación para mejorar la visibilidad de caracteres
    kernel = np.ones((3, 3), np.uint8)
    eroded = cv2.erode(adaptive_thresh, kernel, iterations=1)
    dilated = cv2.dilate(eroded, kernel, iterations=1)
    
    # Retornar opciones para pruebas posteriores
    return adaptive_thresh, binary_thresh, dilated, image

# Función para dividir la imagen en regiones usando contornos
# Añadido: Pre-segmentación mejorada

def divide_image_regions(image):
    contours, _ = cv2.findContours(image, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    height, width = image.shape[:2]

    # Asumimos que el texto impreso está en la mitad superior y manuscrito en la inferior
    contour_areas = [(cv2.boundingRect(c), cv2.contourArea(c)) for c in contours]
    sorted_contours = sorted(contour_areas, key=lambda x: x[1], reverse=True)

    digital_text_region = image[:int(height * 0.6), :]  # Estimación inicial de área digital
    handwritten_text_region = image[int(height * 0.6):, :]  # Estimación inicial de área manuscrita

    if len(sorted_contours) > 0:
        # Usar el contorno más grande para delimitar la región principal de texto
        (x, y, w, h), _ = sorted_contours[0]
        if y < height * 0.5:
            digital_text_region = image[y:y+h, x:x+w]
        else:
            handwritten_text_region = image[y:y+h, x:x+w]

    return digital_text_region, handwritten_text_region

# Función para extraer texto digital con Tesseract
# Añadido: Pruebas con diferentes configuraciones de Tesseract

def extract_text_tesseract(region):
    config_options = ['--psm 4', '--psm 6', '--psm 11']  # Diferentes configuraciones para probar
    texts = []
    for config in config_options:
        config_with_options = f"{config} -c preserve_interword_spaces=1"
        text = pytesseract.image_to_string(region, lang='spa', config=config_with_options)
        texts.append(text)
    
    # Seleccionar el texto más largo como el más probable
    return max(texts, key=len)

# Función para extraer texto manuscrito con EasyOCR
# Añadido: Preprocesamiento adicional para EasyOCR

def extract_text_easyocr(region):
    # Aplicar más preprocesamiento antes de pasar a EasyOCR
    contrasted = cv2.convertScaleAbs(region, alpha=1.5, beta=20)
    reader = easyocr.Reader(['es'], gpu=True)
    results = reader.readtext(contrasted, detail=0, paragraph=True)
    text = '\n'.join(results)
    return text

# Función para extraer texto manuscrito con PaddleOCR
# Añadido: PaddleOCR para comparar resultados

def extract_text_paddleocr(region):
    ocr = PaddleOCR(use_angle_cls=True, lang='es')
    results = ocr.ocr(region, cls=True)
    texts = [line[-1][0] for line in results if len(line) > 0]
    text = '\n'.join(texts)
    return text

# Función para extraer texto con modelo de deep learning (TPS-ResNet-BiLSTM-Attn)
def extract_text_deep_learning(image, model_path, opt):
    device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
    model = Model(opt).to(device)
    
    # Cargar el estado del modelo
    state_dict = torch.load(model_path, map_location=device)
    # Ajustar nombres de las claves si es necesario (por ejemplo, si fueron entrenados en DataParallel)
    new_state_dict = OrderedDict()
    for k, v in state_dict.items():
        name = k.replace('module.', '') if 'module.' in k else k  # eliminar 'module.'
        new_state_dict[name] = v
    model.load_state_dict(new_state_dict, strict=False)
    model.eval()

    # Preprocesamiento de la imagen
    image = cv2.resize(image, (opt.imgW, opt.imgH))
    image = np.expand_dims(image, axis=0) / 255.0  # Añadir dimensión de canal
    image = torch.FloatTensor(image).unsqueeze(0).to(device)

    # Generar predicción
    length_for_pred = torch.IntTensor([opt.batch_max_length] * 1).to(device)
    text_for_pred = torch.zeros(1, opt.batch_max_length + 1).long().to(device)

    with torch.no_grad():
        preds = model(image, text_for_pred, is_train=False)
        _, preds_index = preds.max(2)
        preds_str = []
        for idx in preds_index:
            preds_str.append(''.join([chr(i) for i in idx if i != 0]))
    
    return ''.join(preds_str)

# Función para procesar el texto y eliminar saltos de línea injustificados
def process_text(text):
    # Reemplazar saltos de línea simples dentro de párrafos por espacios
    text = re.sub(r'(?<!\n)\n(?!\n)', ' ', text)
    # Reemplazar múltiples saltos de línea (indicando nuevos párrafos) por dos saltos de línea
    text = re.sub(r'\n\s*\n+', '\n\n', text)
    # Eliminar espacios múltiples
    text = re.sub(r'[ ]{2,}', ' ', text)
    # Eliminar espacios antes y después de saltos de línea
    text = re.sub(r' *\n', '\n', text)
    text = re.sub(r'\n *', '\n', text)
    # Eliminar caracteres no compatibles con XML
    text = re.sub(r'[\x00-\x1F\x7F]', '', text)
    # Eliminar espacios al inicio y al final
    text = text.strip()
    return text

# Función para guardar el texto en un archivo Word sin divisiones
def save_to_word(text, output_path):
    doc = Document()
    paragraphs = text.split('\n\n')
    for paragraph in paragraphs:
        doc.add_paragraph(paragraph)
    doc.save(output_path)

# Programa principal
if __name__ == "__main__":
    image_path = 'imagen_5.png'  # Cambia esto por la ruta de tu imagen
    output_path = 'texto_extraido.docx'  # Nombre del archivo de salida
    model_path = 'TPS-ResNet-BiLSTM-Attn.pth'  # Ruta del modelo preentrenado

    # Definición de opciones (opt) para el modelo
    class Options:
        Transformation = 'TPS'
        FeatureExtraction = 'ResNet'
        SequenceModeling = 'BiLSTM'
        Prediction = 'Attn'
        num_fiducial = 20
        input_channel = 1  # Cambiado a 1 para imágenes en escala de grises
        output_channel = 512
        hidden_size = 256
        num_class = 38  # Ajustado para coincidir con el modelo preentrenado
        imgH = 32
        imgW = 100
        batch_max_length = 25

    opt = Options()

    try:
        gray_image, binary_thresh, dilated_image, original_image = preprocess_image(image_path)
        digital_region, handwritten_region = divide_image_regions(gray_image)
        
        # Extraer texto digital con Tesseract
        text_digital_raw = extract_text_tesseract(digital_region)
        
        # Extraer texto manuscrito con EasyOCR
        text_handwritten_easyocr = extract_text_easyocr(handwritten_region)
        
        # Extraer texto manuscrito con PaddleOCR
        text_handwritten_paddleocr = extract_text_paddleocr(handwritten_region)
        
        # Combinar los textos (preferir el texto más largo entre EasyOCR y PaddleOCR)
        text_handwritten_raw = max([text_handwritten_easyocr, text_handwritten_paddleocr], key=len)
        
        combined_text = text_digital_raw.strip() + '\n\n' + text_handwritten_raw.strip()
        
        # Procesar el texto
        processed_text = process_text(combined_text)
        
        # Guardar en archivo Word
        save_to_word(processed_text, output_path)
        print(f"Texto guardado en: {output_path}")
    except ValueError as e:
        print(e)
