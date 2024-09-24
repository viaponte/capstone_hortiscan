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
  username: string = 'newuser2';
  password: string = 'password123';

  constructor(private router: Router, private authService: AuthService) {}

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
}
