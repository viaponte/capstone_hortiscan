import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../services/authservice/authservice.service';
import { NotificationService } from '../../services/notificationservice/notification.service'; // Asegúrate de que esta ruta sea correcta
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, IonicModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})

export class HeaderComponent {
  isFoldersPage: boolean = true;
  modalOpen: boolean = false;  // Variable para manejar el estado del modal
  notifications: { message: string; date: Date }[] = [];  // Lista de notificaciones

  constructor(private loginService: AuthService, private router: Router, private notificationService: NotificationService) {
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.isFoldersPage = event.url === '/folders';
      });

    // Simulación de notificaciones de ejemplo
    this.notifications = [
      { message: 'Nueva actualización disponible', date: new Date() },
      { message: 'Reunión programada para mañana', date: new Date() },
      { message: 'Documento escaneado correctamente', date: new Date() },
    ];
  }

  // Método para guardar una notificación
  saveNotification(message: string) {
    this.notificationService.saveNotification(message).subscribe(
      (response: any) => { // Añadir el tipo `any` explícitamente
        console.log('Notificación guardada:', response);
        // Aquí puedes actualizar la lista de notificaciones si lo deseas
        this.notifications.push({ message, date: new Date() });
      },
      (error: any) => { // Añadir el tipo `any` explícitamente
        console.error('Error al guardar la notificación:', error);
      }
    );
  }

  // Abre el modal
  openModal() {
    this.modalOpen = true;
  }

  // Cierra el modal
  closeModal() {
    this.modalOpen = false;
  }

  // Cierra la sesión
  logout() {
    this.loginService.logout();
    this.router.navigate(['/login']);
  }
}
