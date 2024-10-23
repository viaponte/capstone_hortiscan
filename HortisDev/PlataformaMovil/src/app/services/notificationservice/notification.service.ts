import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';  
import { AuthService } from '../authservice/authservice.service'; // Asegúrate de que este servicio también esté disponible en el móvil

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private apiUrl = environment.apiUrl;

  username: string | null = null;


  constructor(private http: HttpClient, private authService: AuthService) {
    this.username = this.authService.getUsername();
   }

   saveNotification(message: string): Observable<any> {
    const notificationData = { 
      idUsuario: this.authService.getUsername(), // Asegúrate de tener una función para obtener el ID del usuario
      mensajeNotificacion: message, 
      fechaNotificacion: new Date().toISOString() // Envíalo como string ISO
    };
  
    return this.http.post(`${this.apiUrl}/api/notificaciones/guardar`, notificationData); 
  }
  
  
}