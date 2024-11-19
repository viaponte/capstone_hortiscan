import { Component, OnInit, ViewChild } from '@angular/core';
import { HeaderComponent } from '../../shared/common/header/header.component';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuarioservice/usuario.service';
import { AuthService } from '../../services/authservice/authservice.service';
import { FormsModule } from '@angular/forms';
import { CarpetaDTO } from '../../models/CarpetaDTO';
import { SyncService } from '../../services/syncservice/sync.service';
import { NotificacionService } from '../../services/notificacionservice/notificacion.service'; // Asegúrate de que la ruta sea correcta
import { NotificacionDTO } from '../../models/NotificacionDTO'; // Asegúrate de que esta ruta sea correcta
import { NotificacionhelperService } from '../../services/notificacionHelperService/notificacionhelper.service';

import { CarouselComponent } from '../../shared/common/carousel/carousel.component';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [HeaderComponent, RouterModule, FormsModule, CommonModule, CarouselComponent],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss',
  providers: []
})
export class MainComponent implements OnInit {
  @ViewChild('deleteConfirmationModal') deleteConfirmationModal: any;


  carpetas: CarpetaDTO[] = []; // Variable para almacenar las carpetas
  archivos: string[] = []; // Variable para almacenar los archivos dentro de la carpeta
  folderName: string = '';  // Variable para almacenar el nombre de la carpeta
  username: string | null = '';  // Variable para almacenar el nombre de usuario
  carpetaSeleccionada: CarpetaDTO | null = null; // Almacenar la carpeta seleccionada

  constructor(
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private syncService: SyncService,
    private notificacionHelperService: NotificacionhelperService,
    private notificacionService: NotificacionService
  ) {
    this.username = this.authService.getUsername();
  }

  ngOnInit(): void {
    if (this.username) {
      this.loadCarpetas();
    } else {
      alert('Usuario no autenticado');
    }
  }

  // Método para crear una nueva carpeta
  crearCarpeta() {
    if (this.folderName.trim() === '') {
      alert('El nombre de la carpeta no puede estar vacío');
      return;
    }

    if (!this.username) {
      alert('El usuario no está autenticado');
      return;
    }

    const carpetaDTO = {
      idUsuario: null,
      nombreCarpeta: this.folderName,
      rutaCarpeta: '',
      fechaCreacionCarpeta: null,
      imagenes: []
    };

    this.usuarioService.crearCarpeta(this.username, carpetaDTO.nombreCarpeta).subscribe(
      (response) => {
        const mensajeNotificacion = `Carpeta "${this.folderName}" creada exitosamente.`;
        this.notificacionHelperService.crearNotificacion(mensajeNotificacion);

        this.loadCarpetas();
        this.folderName = '';  // Limpiar el campo de entrada
      },
      error => {
        console.error('Error al crear la carpeta:', error);
        alert('Error al crear la carpeta');
      }
    );
  }

  confirmDelete(nombreCarpeta: string) {
    this.carpetaSeleccionada = this.carpetas.find(carpeta => carpeta.nombreCarpeta === nombreCarpeta) || null;
    if (!this.carpetaSeleccionada) {
      alert("Carpeta no encontrada.");
    }
  }

  // Método para cargar las carpetas del usuario
  loadCarpetas() {
    this.usuarioService.getCarpetas(this.username!).subscribe(
      (response) => {
        this.carpetas = response;
        this.syncService.initSyncCarpetas();
      },
      (error) => {
        console.error('Error al cargar las carpetas:', error);
      }
    );
  }

  
  deleteCarpeta(modal: any) {
    if (this.carpetaSeleccionada) {
      this.usuarioService.deleteCarpeta(this.carpetaSeleccionada.nombreCarpeta).subscribe(
        (response) => {
          console.log('Carpeta eliminada con éxito', response);
          this.loadCarpetas(); // Recargar las carpetas
          modal.hide(); // Cerrar el modal
          const mensajeNotificacion = `Carpeta "${this.carpetaSeleccionada!.nombreCarpeta}" eliminada exitosamente.`;
          this.notificacionHelperService.crearNotificacion(mensajeNotificacion);
        },
        (error) => {
          console.error('Error al eliminar la carpeta', error);
          alert('Hubo un error al eliminar la carpeta.');
        }
      );
    }
  }
}
