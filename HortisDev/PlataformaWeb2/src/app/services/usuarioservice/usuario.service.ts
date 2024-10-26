import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { apiUrl } from '../../enviroment/enviroment';
import { CarpetaDTO } from '../../models/CarpetaDTO';
import { AuthService } from '../authservice/authservice.service';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  constructor(private http: HttpClient, private authService: AuthService) {
    this.username = this.authService.getUsername();
  }

  username: string | null = null;

  // Método para crear la carpeta
  crearCarpeta(username: string, folderName: string): Observable<any> {
    const url = `${apiUrl}/api/usuario/${username}/crear-carpeta`;

    // Asegúrate de enviar un objeto con el nombre de la carpeta como string
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

  getImagePath(nombreCarpeta: string, fileName: string): Observable<Blob> {
    const request = `${apiUrl}/api/usuario/${this.username}/carpeta/${nombreCarpeta}/archivo/${fileName}`;

    return this.http.get(request, { responseType: 'blob' }); // Cambiamos a 'blob'
  }

  // Método para eliminar carpeta y su contenido
  deleteCarpeta(nombreCarpeta: string): Observable<any> {
    const request = `${apiUrl}/api/usuario/${this.username}/carpeta/${nombreCarpeta}`;

    return this.http.delete(request);
  }

  // Método para eliminar imagen
  deleteImagen(nombreCarpeta: string, fileName: string): Observable<any> {
    const request = `${apiUrl}/api/imagen/${this.username}/carpeta/${nombreCarpeta}/imagen/${fileName}`;

    return this.http.delete(request);
  }

  // Método para obtener el PDF de un archivo Word
  getPdfPath(nombreCarpeta: string, fileName: string, timestamp: number): Observable<Blob> {
    const request = `${apiUrl}/api/convert/${this.username}/carpeta/${nombreCarpeta}/archivo/${fileName}/libreoffice?ts=${timestamp}`;
    return this.http.get(request, { responseType: 'blob' }).pipe(
      catchError(error => {
        console.error('Error al obtener el PDF:', error);
        return throwError(() => new Error('Error al obtener el PDF.'));
      })
    );
  }
  
  
}