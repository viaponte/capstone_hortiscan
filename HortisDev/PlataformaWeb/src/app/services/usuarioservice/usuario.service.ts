import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiUrl } from '../../enviroment/enviroment';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  constructor(private http: HttpClient) { }

  // Método para crear la carpeta
  crearCarpeta(username: string, folderName: string): Observable<any> {
    const url = `${apiUrl}/api/usuario/${username}/crear-carpeta`;
    
    // Asegúrate de enviar un objeto con el nombre de la carpeta como string
    const body = { folderName: folderName };
  
    return this.http.post(url, body);
  }  
}