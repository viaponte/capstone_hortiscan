import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { apiUrl } from '../../enviroment/enviroment';
import { Observable, Subject } from 'rxjs';
import { AuthService } from '../authservice/authservice.service';
import { NotificacionDTO } from '../../models/NotificacionDTO';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class NotificacionService {
  private apiUrl = apiUrl;
  private notificacionCreada = new Subject<NotificacionDTO>();
  username: string | null = null;

  constructor(private http: HttpClient, private authService: AuthService) {
    this.username = this.authService.getUsername();
  }

  // Método para obtener las notificaciones del usuario autenticado
  obtenerNotificaciones(): Observable<NotificacionDTO[]> {
    if (!this.username) {
      throw new Error('No hay usuario autenticado');
    }
    return this.http.get<NotificacionDTO[]>(`${this.apiUrl}/api/notificacion/${this.username}/notificaciones`);
  }

  // Método para eliminar una notificación específica
  eliminarNotificacion(idNotificacion: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/notificacion/${this.username}/notificacion/${idNotificacion}`);
  }

  // Método para crear una nueva notificación
  crearNotificacion(notificacionDTO: NotificacionDTO): Observable<NotificacionDTO> {
    return this.http.post<NotificacionDTO>(`${this.apiUrl}/api/notificacion/${this.username}`, notificacionDTO).pipe(
      tap((nuevaNotificacion) => {
        this.notificacionCreada.next(nuevaNotificacion); // Emitir la nueva notificación
      })
    );
  }

  // Observable para escuchar cuando se crea una nueva notificación
  getNuevaNotificacion(): Observable<NotificacionDTO> {
    return this.notificacionCreada.asObservable();
  }
}
