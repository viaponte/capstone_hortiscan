import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment'; // Asegúrate de que esta ruta sea correcta para el móvil


@Injectable({
  providedIn: 'root'  // Registra el servicio en el nivel de la aplicación
})
export class OcrService {
  apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Método para enviar la imagen a la API de OCR
  processImage(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('image', file);

    // Realiza la solicitud POST a la API de OCR
    return this.http.post(this.apiUrl, formData);
  }

  // Método para subir la imagen al backend y procesarla con OCR
  // uploadImage(image: File): Observable<any> {
  //   const formData = new FormData();
  //   formData.append('image', image);

  //   return this.http.post(`${this.apiUrl}/process`, formData, {
  //     responseType: 'text'
  //   });
  // }
  uploadImage(image: File): Observable<any> {
    const formData = new FormData();
    formData.append('image', image);

    return this.http.post(`${environment.apiUrl}/api/imagen/process-ocr`, formData, {
      responseType: 'text'
    });
}
}