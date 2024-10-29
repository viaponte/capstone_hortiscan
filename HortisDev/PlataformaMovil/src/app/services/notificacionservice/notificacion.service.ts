import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, Subject } from 'rxjs';
import { AuthService } from '../authservice/authservice.service';
import { NotificacionDTO } from '../../models/NotificacionDTO'; // Asegúrate de que la ruta sea correcta
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class NotificacionService {
  private apiUrl = environment.apiUrl;
  private notificacionCreada = new Subject<NotificacionDTO>(); // Subject para nuevas notificaciones
  username: string | null = null;

  constructor(private http: HttpClient, private authService: AuthService) {
    this.username = this.authService.getUsername();
  }

  obtenerNotificaciones(): Observable<NotificacionDTO[]> {
    if (!this.username) {
      throw new Error('No hay usuario autenticado');
    }
    return this.http.get<NotificacionDTO[]>(`${this.apiUrl}/api/notificacion/${this.username}/notificaciones`);
  }

  eliminarNotificacion(idNotificacion: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/notificacion/${this.username}/notificacion/${idNotificacion}`);
  }

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
