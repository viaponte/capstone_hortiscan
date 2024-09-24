import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UsuarioService } from '../../services/usuarioservice/usuario.service'; // Asegúrate de que la ruta sea correcta
import { AuthService } from '../../services/authservice/authservice.service'; // Asegúrate de que la ruta sea correcta
import { CarpetaDTO } from '../../models/CarpetaDTO';
import { NavController } from '@ionic/angular';

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

  constructor(private router: Router, private usuarioService: UsuarioService, private authService: AuthService, private navCtrl: NavController) {
    this.username = this.authService.getUsername();
  }

  ngOnInit() {
    if (this.username) {
      this.loadCarpetas();
    } else {
      alert('Usuario no autenticado');
    }
  }

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

  logout() {
    this.authService.logout(); // Llama al método logout del AuthService
    this.navCtrl.navigateRoot('/login'); // Usar navigateRoot para redirigir al login
  }
  
  createFolder() {
    this.navCtrl.navigateForward('/create-folder');
  }
  
  

}

