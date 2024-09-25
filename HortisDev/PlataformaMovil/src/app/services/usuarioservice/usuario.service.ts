import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { apiUrl } from '../../../environments/environment'; // Asegúrate de que esta ruta sea correcta para el móvil
import { CarpetaDTO } from '../../models/CarpetaDTO';
import { AuthService } from '../authservice/authservice.service'; // Asegúrate de que este servicio también esté disponible en el móvil

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  username: string | null = null;

  constructor(private http: HttpClient, private authService: AuthService) {
    this.username = this.authService.getUsername();
  }

  // Método para crear la carpeta
  crearCarpeta(username: string, folderName: string): Observable<any> {
    const url = `${apiUrl}/api/usuario/${username}/crear-carpeta`;
    const body = { folderName: folderName };
    return this.http.post(url, body);
  }  

  // Método para obtener las carpetas del usuario
  getCarpetas(username: string): Observable<CarpetaDTO[]> {
    return this.http.get<CarpetaDTO[]>(`${apiUrl}/api/usuario/${username}/carpetas`);
  }

  // Método para obtener el contenido de una carpeta específica
  getCarpetaContenido(username: string, nombreCarpeta: string): Observable<string[]> {
    return this.http.get<string[]>(`${apiUrl}/api/usuario/${username}/carpeta/${nombreCarpeta}/contenido`);
  }

  // Método para obtener la ruta de la imagen
  getImagePath(nombreCarpeta: string, fileName: string): Observable<string> {
    const request = `${apiUrl}/api/usuario/${this.username}/carpeta/${nombreCarpeta}/archivo/${fileName}`;
    console.log(request);
    return this.http.get(request, { responseType: 'blob' }).pipe(
      map(blob => URL.createObjectURL(blob))
    );
  }  

  uploadImage(file: File, folderName: string): Observable<any> {
    const formData: FormData = new FormData();
    console.log(file)

    // Adjuntamos el archivo y los otros parámetros
    formData.append('file', file);
    formData.append('folderName', folderName);

    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data'); // No es estrictamente necesario, Angular maneja FormData automáticamente

    
    // Realizamos la solicitud POST
    const request = `${apiUrl}/api/imagen/subir/${this.username}`
    return this.http.post(request, formData, { headers });
  }
}