import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../services/authservice/authservice.service';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  imports: [CommonModule, FormsModule, HttpClientModule, RouterModule, IonicModule]
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
          if (response && response.jtw) {
            this.authService.saveToken(response.jtw);
            this.router.navigate(['/folders']);
          }
        });
    }
  }
}
