import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UsuarioService } from '../../services/usuarioservice/usuario.service';
import { AuthService } from '../../services/authservice/authservice.service';
import { CarpetaDTO } from '../../models/CarpetaDTO';
import { NavController } from '@ionic/angular';
import { ReloadService } from 'src/app/services/reloadservice/reload.service';
<<<<<<< HEAD
import { NotificationService } from 'src/app/services/notificationservice/notification.service';
=======
import { NotificacionService } from '../../services/notificacionservice/notificacion.service'; // Asegúrate de que la ruta sea correcta
import { NotificacionDTO } from '../../models/NotificacionDTO'; // Asegúrate de que esta ruta sea correcta
>>>>>>> develop

@Component({
  selector: 'app-folders',
  templateUrl: './folders.page.html',
  styleUrls: ['./folders.page.scss'],
})
export class FoldersPage implements OnInit {
  carpetas: CarpetaDTO[] = []; // Variable para almacenar las carpetas
  archivos: string[] = []; // Variable para almacenar los archivos dentro de la carpeta
  folderName: string = '';  // Variable para almacenar el nombre de la carpeta
  username: string | null = '';  // Variable para almacenar el nombre de usuario
  carpetaSeleccionada: CarpetaDTO | null = null; // Almacenar la carpeta seleccionada
  modalOpen: boolean = false;  // Variable para manejar el estado del modal

  constructor(
    private router: Router,
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private navCtrl: NavController,
    private reloadService: ReloadService,
    private cdr: ChangeDetectorRef,
<<<<<<< HEAD
    private notificationService: NotificationService
=======
    private notificacionService: NotificacionService,
>>>>>>> develop
  ) {
    this.username = this.authService.getUsername();
  }

  ngOnInit() {
    if (this.username) {
      this.loadCarpetas();
    } else {
      alert('Usuario no autenticado');
    }
  }

  deleteCarpeta(nombreCarpeta: string) {
    this.usuarioService.deleteCarpeta(nombreCarpeta).subscribe(
      (response) => {
        this.loadCarpetas();
<<<<<<< HEAD
        console.log('Carpeta eliminada con exito', response);
=======
        console.log('Carpeta eliminada con éxito', response);
  
        // Crear un mensaje de notificación para la eliminación
        const mensajeNotificacion = `Carpeta "${nombreCarpeta}" eliminada exitosamente.`;
        const notificacionDTO: NotificacionDTO = {
          idNotificacion: 0, // El backend lo generará automáticamente
          mensajeNotificacion: mensajeNotificacion,
          fechaNotificacion: new Date().toISOString(), // Fecha actual en formato ISO
        };
  
        // Llamar a NotificacionService para crear la notificación
        this.notificacionService.crearNotificacion(notificacionDTO).subscribe(
          (notificacionResponse) => {
            console.log('Notificación de eliminación creada:', notificacionResponse);
          },
          (error) => {
            console.error('Error al crear la notificación de eliminación:', error);
          }
        );
>>>>>>> develop
      },
      (error) => {
        console.error('Error al eliminar la carpeta', error);
      }
    );
  }
<<<<<<< HEAD
=======
  
>>>>>>> develop

  // Método para cargar las carpetas del usuario desde el backend
  loadCarpetas() {
    this.usuarioService.getCarpetas(this.username!).subscribe(
      (response) => {
        this.carpetas = response;  // Cargar las carpetas en la variable
      },
      (error) => {
        console.error('Error al cargar las carpetas:', error);
      }
    );
  }

  openFolder(folderName: string) {
    this.router.navigate([`/folder-content/${folderName}`]);
  }

  // Abre el modal para crear carpeta
  openModal() {
    this.modalOpen = true;
  }

  // Cierra el modal
  closeModal() {
    this.modalOpen = false;
  }

  // Método para crear una nueva carpeta
  crearCarpeta() {
    if (this.folderName.trim() === '') {
      alert('El nombre de la carpeta no puede estar vacío');
      return;
    }

    const carpetaDTO = {
      idUsuario: null, // El backend establecerá este valor
      nombreCarpeta: this.folderName,
      rutaCarpeta: '', // El backend generará la ruta
      fechaCreacionCarpeta: null, // El backend establecerá la fecha
      imagenes: []  // Inicialmente vacío
    };

    this.usuarioService.crearCarpeta(this.username!, carpetaDTO.nombreCarpeta).subscribe(
      (response) => {
<<<<<<< HEAD
        this.folderName = '';  // Limpia el input después de crear la carpeta
        this.closeModal();  // Cierra el modal después de crear la carpeta
        this.loadCarpetas(); // Recarga las carpetas para que la nueva aparezca
        
        // Guarda la notificación
        this.notificationService.saveNotification(`Carpeta '${carpetaDTO.nombreCarpeta}' creada con éxito.`).subscribe(
          (notificationResponse) => {
            console.log('Notificación guardada:', notificationResponse);
            alert('Holaaaa');
          },
          (error) => {
            console.error('Error al guardar la notificación:', error);
            alert('Holaaaa errorrrrr');
          }
        );
        
=======
        const mensajeNotificacion = `Carpeta "${this.folderName}" creada exitosamente.`;
        const notificacionDTO: NotificacionDTO = {
          idNotificacion: 0, // Se generará automáticamente en el backend
          mensajeNotificacion: mensajeNotificacion,
          fechaNotificacion: new Date().toISOString(), // O un formato de fecha que prefieras
        };

        this.notificacionService.crearNotificacion(notificacionDTO).subscribe(
          (notificacionResponse) => {
            console.log('Notificación creada:', notificacionResponse);
          },
          (error) => {
            console.error('Error al crear la notificación:', error);
          }
        );

        this.folderName = '';  // Limpia el input después de crear la carpeta
        this.closeModal();  // Cierra el modal después de crear la carpeta
        this.loadCarpetas(); // Recarga las carpetas para que la nueva aparezca

        // Aquí se crea la notificación


>>>>>>> develop
      },
      error => {
        console.error('Error al crear la carpeta:', error);
        alert('Error al crear la carpeta');
      }
    );
  }

  handleRefresh(event: any) {
    this.reloadService.handleRefresh(event).then(() => {
      this.cdr.detectChanges();
      this.loadCarpetas();
    });
  }
}
