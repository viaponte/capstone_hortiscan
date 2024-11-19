import { Component, EventEmitter, Input, Output, ChangeDetectorRef, HostListener } from '@angular/core';
import { NavigationEnd, Router, Event } from '@angular/router';
import { AuthService } from '../../../services/authservice/authservice.service';
import { Location, CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { NotificacionDTO } from '../../../models/NotificacionDTO';
import { NotificacionService } from '../../../services/notificacionservice/notificacion.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [HeaderComponent, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  @Input() folderName?: string;
  @Output() backClicked = new EventEmitter<void>();
  isDropdownOpen = false; // Estado para controlar si el panel está abierto

  showBackButton: boolean = true;
  isInMenu: boolean = false; // Variable para controlar si estás en el menú
  notificaciones: NotificacionDTO[] = [];
  username: string | null = '';  // Variable para almacenar el nombre de usuario


  constructor(
    private loginService: AuthService,
    private router: Router,
    private location: Location,
    private notificacionService: NotificacionService,
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
  ) {
    this.username = this.authService.getUsername();
    // Escuchar los cambios de ruta y actualizar showBackButton e isInMenu
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: Event) => {
        if (event instanceof NavigationEnd) {
          this.showBackButton = event.url !== '/menu';
          this.isInMenu = event.url === '/menu'; // Actualizar el estado de isInMenu
        }
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

  eliminarNotificacion(idNotificacion: number, event: MouseEvent): void {
    event.stopPropagation(); // Esto previene que el evento cierre el dropdown

    this.notificacionService.eliminarNotificacion(idNotificacion).subscribe(
      () => {
        this.notificaciones = this.notificaciones.filter(n => n.idNotificacion !== idNotificacion);
      },
      (error) => {
        console.error('Error al eliminar la notificación', error);
      }
    );
  }

  toggleDropdown(event: MouseEvent): void {
    event.stopPropagation(); // Evita propagación
    this.isDropdownOpen = !this.isDropdownOpen; // Alterna la visibilidad
  }

  logout() {
    this.loginService.logout();
    this.router.navigate(['/login']);
  }

  goBack(): void {
    if (this.folderName) {
      this.backClicked.emit();
    } else {
      this.router.navigate(['/menu']);
    }
  }

  goEditor() {
    this.router.navigate(['/editor']);
  }
}
