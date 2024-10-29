import { ChangeDetectorRef, Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../services/authservice/authservice.service';
import { ReloadService } from 'src/app/services/reloadservice/reload.service';
import { NotificacionService } from '../../services/notificacionservice/notificacion.service';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { NotificacionDTO } from '../../models/NotificacionDTO';

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
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.isFoldersPage = event.url === '/folders';
      });
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
      }
    );
  }

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
  }

  handleRefresh(event: any) {
    this.reloadService.handleRefresh(event).then(() => {
      this.cdr.detectChanges();
      this.loadNotificaciones();
    });
  }
}

