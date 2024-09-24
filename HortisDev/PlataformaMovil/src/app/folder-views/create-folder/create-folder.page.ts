import { Component, OnInit } from '@angular/core';
import { NavController } from '@ionic/angular';
import { UsuarioService } from '../../services/usuarioservice/usuario.service'; // Asegúrate de que la ruta sea correcta
import { AuthService } from '../../services/authservice/authservice.service'; // Asegúrate de que la ruta sea correcta
import { CarpetaDTO } from '../../models/CarpetaDTO';

@Component({
  selector: 'app-create-folder',
  templateUrl: './create-folder.page.html',
  styleUrls: ['./create-folder.page.scss'],
})
export class CreateFolderPage implements OnInit {
  carpetas: CarpetaDTO[] = []; // Variable para almacenar las carpetas
  archivos: string[] = []; // Variable para almacenar los archivos dentro de la carpeta
  folderName: string = '';  // Variable para almacenar el nombre de la carpeta
  username: string | null = '';  // Variable para almacenar el nombre de usuario
  carpetaSeleccionada: CarpetaDTO | null = null; // Almacenar la carpeta seleccionada

  constructor(
    private navCtrl: NavController,
    private usuarioService: UsuarioService,
    private authService: AuthService
  ) {
    this.username = this.authService.getUsername(); // Obtenemos el nombre de usuario autenticado
  }

  ngOnInit() {
    // Verificar si el usuario está autenticado antes de permitir crear carpetas
    if (!this.username) {
      alert('Usuario no autenticado');
      this.navCtrl.navigateRoot('/login'); // Redirige al login si no está autenticado
    }
  }

  // Método para crear una nueva carpeta llamando al servicio
  crearCarpeta() {
    if (this.folderName.trim() === '') {
      alert('El nombre de la carpeta no puede estar vacío');
      return;
    }
  
    // Asegúrate de que el username esté disponible
    if (!this.username) {
      alert('El usuario no está autenticado');
      return;
    }
  
    const carpetaDTO = {
      idUsuario: null, // El backend establecerá este valor
      nombreCarpeta: this.folderName,
      rutaCarpeta: '', // El backend generará la ruta
      fechaCreacionCarpeta: null, // El backend establecerá la fecha
      imagenes: []  // Inicialmente vacío
    };
  
    this.usuarioService.crearCarpeta(this.username, carpetaDTO.nombreCarpeta).subscribe(
      response => {
        alert('Carpeta creada exitosamente');
        this.folderName = '';  // Limpia el input después de crear la carpeta
        this.navCtrl.navigateBack('/folders'); // Redirige a la vista de carpetas después de crearla
      },
      error => {
        console.error('Error al crear la carpeta:', error);
        alert('Error al crear la carpeta');
      }
    );
  }
}
