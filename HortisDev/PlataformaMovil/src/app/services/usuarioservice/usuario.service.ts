import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment'; // Asegúrate de que esta ruta sea correcta para el móvil
import { CarpetaDTO } from '../../models/CarpetaDTO';
import { AuthService } from '../authservice/authservice.service'; // Asegúrate de que este servicio también esté disponible en el móvil

@Injectable({
  providedIn: 'root'
})


export class UsuarioService {


  apiUrl = environment.apiUrl;

  username: string | null = null;

  constructor(private http: HttpClient, private authService: AuthService) {
    this.username = this.authService.getUsername();
  }
  // Método para subir el archivo Word
  uploadWord(file: File, folderName: string): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file); // Agregar el archivo Word
    formData.append('folderName', folderName); // Agregar el nombre de la carpeta

    // Corrige la sintaxis de la cadena de plantilla para la URL
    const request = `${this.apiUrl}/api/imagen/upload/word/${this.username}`;
    console.log('URL de subida del documento Word:', request);  // Verifica que la URL sea correcta

    return this.http.post(request, formData);
  }

  deleteCarpeta(nombreCarpeta: string): Observable<any> {
    const request = `${this.apiUrl}/api/usuario/${this.username}/carpeta/${nombreCarpeta}`;

    return this.http.delete(request);
  }

  // Método para eliminar imagen
  deleteImagen(nombreCarpeta: string, fileName: string): Observable<any> {
    const request = `${this.apiUrl}/api/imagen/${this.username}/carpeta/${nombreCarpeta}/imagen/${fileName}`;

    return this.http.delete(request);
  }

  // Método para crear la carpeta
  crearCarpeta(username: string, folderName: string): Observable<any> {
    const url = `${this.apiUrl}/api/usuario/${username}/crear-carpeta`;
    const body = { folderName: folderName };
    return this.http.post(url, body);
  }

  // Método para obtener las carpetas del usuario
  getCarpetas(username: string): Observable<CarpetaDTO[]> {
    return this.http.get<CarpetaDTO[]>(`${this.apiUrl}/api/usuario/${username}/carpetas`);
  }

  // Método para obtener el contenido de una carpeta específica
  getCarpetaContenido(username: string, nombreCarpeta: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/api/usuario/${username}/carpeta/${nombreCarpeta}/contenido`);
  }

  // Método para obtener la ruta de la imagen
  getImagePath(nombreCarpeta: string, fileName: string): Observable<string> {
    const request = `${this.apiUrl}/api/usuario/${this.username}/carpeta/${nombreCarpeta}/archivo/${fileName}`;
    console.log(request);
    return this.http.get(request, { responseType: 'blob' }).pipe(
      map(blob => URL.createObjectURL(blob))
    );
  }

  uploadImage(file: File, folderName: string): Observable<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    formData.append('folderName', folderName);

    const request = `${this.apiUrl}/api/imagen/subir/${this.username}`;
    console.log('URL de subida:', request);  // Verifica que la URL sea correcta
    return this.http.post(request, formData);
  }
}