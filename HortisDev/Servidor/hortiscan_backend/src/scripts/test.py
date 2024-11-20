import torch
print(torch.cuda.is_available())  # Debería devolver True si CUDA está habilitado.
print(torch.cuda.get_device_name(torch.cuda.current_device()))  # Debería mostrar el nombre de tu GPU.

# # Programa principal (sin cambios)
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
