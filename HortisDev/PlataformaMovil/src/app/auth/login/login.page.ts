import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../../app/services/authservice/authservice.service';
import { IonicModule } from '@ionic/angular';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [HttpClientModule, RouterModule, FormsModule, CommonModule, IonicModule],
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage {
  username: string = 'lucas';
  password: string = 'lucas';
  isPasswordVisible: boolean = false;

  constructor(private router: Router, private authService: AuthService) { }

  onLogin(form: NgForm) {
    if (form.valid) {
      this.authService.login(this.username, this.password)
        .pipe(
          catchError(error => {
            console.error('Login error', error);
            alert('Logeo fallido. Checkea tus credenciales.');
            return of(null);
          })
        )
        .subscribe(response => {
          if (response && response.jwt) {
            this.authService.saveSession(response.jwt, this.username);
            this.router.navigate(['/folders']); // Ajusta esta ruta según la navegación en tu aplicación móvil
          } else {
            alert('No se pudo recibir el token. Login fallido.');
          }
        });
    }
  }

  togglePasswordVisibility() {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  normalizeUsername(event: Event) {
    const input = event.target as HTMLInputElement;
    let value = input.value.toLowerCase().replace(/[^a-z]/g, ''); // Solo caracteres a-z
    if (value.length > 10) {
      value = value.substring(0, 10); // Limitar a 10 caracteres
    }
    this.username = value;
  }
  

  validatePasswordInput(event: Event) {
    const input = event.target as HTMLInputElement;
    const invalidChars = /[\[\]{\}()=\s]/g; // Caracteres no permitidos, incluyendo espacios
    if (invalidChars.test(input.value)) {
      input.value = input.value.replace(invalidChars, ''); // Elimina caracteres no válidos
      this.password = input.value; // Sincroniza con el modelo
    }
  }
  
  preventInvalidPaste(event: ClipboardEvent) {
    const clipboardData = event.clipboardData?.getData('text') || '';
    const invalidChars = /[\[\]{\}()=\s]/g; // Caracteres no permitidos, incluyendo espacios
    if (invalidChars.test(clipboardData)) {
      event.preventDefault(); // Bloquea el contenido pegado si contiene caracteres inválidos
    }
  }
}
