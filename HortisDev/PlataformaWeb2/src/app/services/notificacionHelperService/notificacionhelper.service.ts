import { Injectable } from '@angular/core';
import { NotificacionService } from '../notificacionservice/notificacion.service';
import { NotificacionDTO } from '../../models/NotificacionDTO';

@Injectable({
  providedIn: 'root'
})
export class NotificacionhelperService {

  constructor(
    private notificacionService: NotificacionService
  ) { }

  crearNotificacion(mensaje: string): void {
    const notificacionDTO: NotificacionDTO = {
      idNotificacion: 0,
      mensajeNotificacion: mensaje,
      fechaNotificacion: new Date().toISOString(),
    };

    this.notificacionService.crearNotificacion(notificacionDTO).subscribe(
      (response) => {
        console.log('Notificación creada:', response);
      },
      (error) => {
        console.error('Error al crear la notificación:', error);
      }
    );
  }
}
