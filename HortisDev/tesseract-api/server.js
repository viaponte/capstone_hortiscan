const express = require('express');
const Tesseract = require('tesseract.js');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
const cors = require('cors');

const app = express();
const upload = multer({ dest: 'uploads/' }); // Carpeta para almacenar las imágenes temporalmente

app.use(cors()); // Permitir CORS para solicitudes de otros orígenes

// Ruta principal para procesar la imagen y extraer el texto
app.post('/api/ocr', upload.single('image'), (req, res) => {
    if (!req.file) {
        return res.status(400).json({ error: 'No se ha proporcionado ninguna imagen' });
    }

    const imagePath = path.join(__dirname, req.file.path);

    Tesseract.recognize(imagePath, 'eng', { logger: m => console.log(m) })
        .then(({ data: { text } }) => {
            // Elimina el archivo temporal después de procesarlo
            fs.unlinkSync(imagePath);
            res.json({ text });
        })
        .catch(err => {
            fs.unlinkSync(imagePath);
            res.status(500).json({ error: 'Error al procesar la imagen', details: err.message });
        });
});

// Inicia el servidor
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Servidor de Tesseract API corriendo en http://localhost:${PORT}`);
});
