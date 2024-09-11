import { Component, OnInit } from '@angular/core';
import { HeaderComponent } from '../../shared/common/header/header.component';
<<<<<<< HEAD
import { UsuarioService } from '../../services/usuarioservice/usuario.service';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/authservice/authservice.service';
=======
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
>>>>>>> ce136559ea4beb99498f43ac5516fd69bf1324f2

@Component({
  selector: 'app-main',
  standalone: true,
<<<<<<< HEAD
  imports: [HeaderComponent, FormsModule],
=======
  imports: [HeaderComponent, RouterModule],
>>>>>>> ce136559ea4beb99498f43ac5516fd69bf1324f2
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss',
  providers: [
    
  ]
})
export class MainComponent {
  folderName: string = '';  // Variable para almacenar el nombre de la carpeta
  username: string | null = '';  // Variable para almacenar el nombre de usuario

  constructor(private usuarioService: UsuarioService, private authService: AuthService) {
    // Almacena el nombre de usuario al inicializar el componente
    this.username = this.authService.getUsername();
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
      },
      error => {
        console.error('Error al crear la carpeta:', error);
        alert('Error al crear la carpeta');
      }
    );
  }  

}
