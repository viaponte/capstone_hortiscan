<<<<<<< HEAD
import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../services/authservice/authservice.service';
import { NotificationService } from '../../services/notificationservice/notification.service'; // Asegúrate de que esta ruta sea correcta
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
=======
import { ChangeDetectorRef, Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../services/authservice/authservice.service';
import { ReloadService } from 'src/app/services/reloadservice/reload.service';
import { NotificacionService } from '../../services/notificacionservice/notificacion.service';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { NotificacionDTO } from '../../models/NotificacionDTO';
>>>>>>> develop

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
<<<<<<< HEAD
  notifications: { message: string; date: Date }[] = [];  // Lista de notificaciones

  constructor(private loginService: AuthService, private router: Router, private notificationService: NotificationService) {
=======
  notificaciones: NotificacionDTO[] = [];
  username: string | null = '';  // Variable para almacenar el nombre de usuario


  constructor(
    private loginService: AuthService,
    private authService: AuthService,
    private router: Router,
    private notificacionService: NotificacionService,
    private reloadService: ReloadService,
    private cdr: ChangeDetectorRef
  ) {
    this.username = this.authService.getUsername();
>>>>>>> develop
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.isFoldersPage = event.url === '/folders';
      });
<<<<<<< HEAD

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
=======
  }

  ngOnInit() {
    if (this.username) {
      this.loadNotificaciones();
      this.notificacionService.getNuevaNotificacion().subscribe((nuevaNotificacion) => {
        this.notificaciones.unshift(nuevaNotificacion); // Añadir al inicio de la lista
        this.cdr.detectChanges(); // Refrescar el componente
      });
    } else {
      alert('Usuario no autenticado');
    }
  }

  loadNotificaciones(): void {
    this.notificacionService.obtenerNotificaciones().subscribe(
      (data: NotificacionDTO[]) => {
        this.notificaciones = data;
      },
      (error) => {
        console.error('Error al obtener las notificaciones', error);
>>>>>>> develop
      }
    );
  }

<<<<<<< HEAD
=======
  eliminarNotificacion(idNotificacion: number): void {
    this.notificacionService.eliminarNotificacion(idNotificacion).subscribe(
      () => {
        this.notificaciones = this.notificaciones.filter(n => n.idNotificacion !== idNotificacion);
      },
      (error) => {
        console.error('Error al eliminar la notificación', error);
      }
    );
  }


>>>>>>> develop
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
<<<<<<< HEAD
    this.router.navigate(['/login']);
  }
}
=======
  }

  handleRefresh(event: any) {
    this.reloadService.handleRefresh(event).then(() => {
      this.cdr.detectChanges();
      this.loadNotificaciones();
    });
  }
}

>>>>>>> develop
