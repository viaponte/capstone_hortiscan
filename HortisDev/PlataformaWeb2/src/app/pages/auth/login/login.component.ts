import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../../services/authservice/authservice.service';
import { SyncService } from '../../../services/syncservice/sync.service';
import { CarouselComponent } from "../../../shared/common/carousel/carousel.component";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [HttpClientModule, RouterModule, FormsModule, CommonModule, CarouselComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username: string = 'lucas'; 
  password: string = 'lucas';
  isPasswordVisible: boolean = false;  // Variable para controlar la visibilidad de la contraseña

  constructor(private router: Router, private authService: AuthService, private syncService: SyncService) {}

  onLogin(form: NgForm) {
    if (form.valid) {
      this.authService.login(this.username, this.password)
        .pipe(
          catchError(error => {
            console.error('Login error', error);
            return of(null);
          })
        )
        .subscribe(response => {
          if (response && response.jwt) {
            this.authService.saveSession(response.jwt, this.username);
            this.syncService.initSyncCarpetas();
            this.router.navigate(['/menu']);
          } else {
            alert('Error al iniciar sesión');
          }
        });
    }
  }  
  // Método para alternar la visibilidad de la contraseña
  togglePasswordVisibility() {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  // Método para normalizar el input de usuario
  normalizeUsername(event: Event) {
    const input = event.target as HTMLInputElement;
  
    // Permitir solo letras minúsculas
    input.value = input.value.toLowerCase().replace(/[^a-z]/g, ''); // Reemplaza cualquier carácter que no sea a-z
    this.username = input.value; // Sincronizar con el modelo
  }
  

  // Método para validar el input de contraseña
  validatePassword(event: KeyboardEvent) {
    const invalidChars = /[\[\]{\}()=]|[\u{1F600}-\u{1F64F}]/gu; // Caracteres especiales y emojis
    if (
      invalidChars.test(event.key) || // Verificar si el carácter está en la lista de prohibidos
      event.key === ' ' // No permitir espacios
    ) {
      event.preventDefault();
    }
  }

  // Método para prevenir pegar contenido inválido en contraseña
  preventInvalidPaste(event: ClipboardEvent) {
    const clipboardData = event.clipboardData?.getData('text') || '';
    const invalidChars = /[\[\]{\}()=]|[\u{1F600}-\u{1F64F}]|\s/gu;
    if (invalidChars.test(clipboardData)) {
      event.preventDefault();
    }
  }
}