import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs'

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  email: string = '';
  password: string = '';

  constructor(private router: Router, private http: HttpClient) {}

  onLogin() {
    const loginData = { username: this.email, password: this.password };

    this.http.post<{ jwt: string}>('http://localhost:8000/auth', loginData)
      .pipe(
        catchError(error => {
          console.error('Login error', error);
          alert('Logeo fallido. Checkea tus credenciales.');
          return of(null);
        })
      )
      .subscribe(response => {
        if (response && response.jwt) {
          // guardar token en el almacenamiento local
          localStorage.setItem('jwt', response.jwt)
          // ir al menú
          this.router.navigate(['/menu'])
        }
      })
  }

}
