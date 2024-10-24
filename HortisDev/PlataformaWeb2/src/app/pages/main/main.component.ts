import { Component, OnInit } from '@angular/core';
import { HeaderComponent } from '../../shared/common/header/header.component';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UsuarioService } from '../../services/usuarioservice/usuario.service';
import { AuthService } from '../../services/authservice/authservice.service';
import { FormsModule } from '@angular/forms';
import { CarpetaDTO } from '../../models/CarpetaDTO';
import { SyncService } from '../../services/syncservice/sync.service';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [HeaderComponent, RouterModule, FormsModule, CommonModule],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss',
  providers: [
    
  ]
})
export class MainComponent implements OnInit {
  carpetas: CarpetaDTO[] = []; // Variable para almacenar las carpetas
  archivos: string[] = []; // Variable para almacenar los archivos dentro de la carpeta
  folderName: string = '';  // Variable para almacenar el nombre de la carpeta
  username: string | null = '';  // Variable para almacenar el nombre de usuario
  carpetaSeleccionada: CarpetaDTO | null = null; // Almacenar la carpeta seleccionada

  constructor(private usuarioService: UsuarioService, private authService: AuthService, private syncService: SyncService) {
    // Almacena el nombre de usuario al inicializar el componente
    this.username = this.authService.getUsername();
  }

  ngOnInit(): void {
    if(this.username) {
      this.loadCarpetas();
      
    } else {
      alert('Usuario no autenticado');
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
      (response) => {
        this.loadCarpetas();
        this.folderName = '';  // Limpia el input después de crear la carpeta
      },
      error => {
        console.error('Error al crear la carpeta:', error);
        alert('Error al crear la carpeta');
      }
    );
  }

  // Método para cargar las carpetas del usuario desde el backend
  loadCarpetas() {
    this.usuarioService.getCarpetas(this.username!).subscribe(
      (response) => {
        this.carpetas = response;  // Cargar las carpetas en la variable
        this.syncService.initSyncCarpetas();
      },
      (error) => {
        console.error('Error al cargar las carpetas:', error);
      }
    );
  }

  // Método para eliminar carpeta y su contenido
  deleteCarpeta(nombreCarpeta: string) {
    this.usuarioService.deleteCarpeta(nombreCarpeta).subscribe(
      (response) => {
        this.loadCarpetas();
        console.log('Carpeta eliminada con exito', response);
      },
      (error) => {
        console.error('Error al eliminar la carpeta', error);
      }
    );
  }
}
