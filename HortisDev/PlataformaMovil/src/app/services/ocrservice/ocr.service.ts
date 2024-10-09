import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'  // Registra el servicio en el nivel de la aplicación
})
export class OcrService {
  private apiUrl = 'http://localhost:3000/api/ocr';

  constructor(private http: HttpClient) {}

  // Método para enviar la imagen a la API de OCR
  processImage(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('image', file);

    // Realiza la solicitud POST a la API de OCR
    return this.http.post(this.apiUrl, formData);
  }
}