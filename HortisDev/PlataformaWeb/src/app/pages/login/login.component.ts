import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../services/authservice/authservice.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [HttpClientModule, RouterModule, FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username: string = 'lucas'; 
  password: string = 'lucas';

  constructor(private router: Router, private authService: AuthService) {}

  onLogin(form: NgForm) {
    console.log(this.authService.isLoggedIn());
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
            this.router.navigate(['/menu']);
          }
        });
    }
  }
}