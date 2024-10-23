import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment'; // Asegúrate de que esta ruta sea correcta
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) { }

  apiUrl = environment.apiUrl;

  login(username: string, password: string): Observable<any> {
    const loginData = { username, password };
    return this.http.post<{ jwt: string }>(`${this.apiUrl}/api/auth/login`, loginData).pipe(
      tap(response => {
        if (response.jwt) {
          this.saveSession(response.jwt, username);
        } else {
          console.error('No se recibió el token JWT en la respuesta');
        }
      })
    );
  }
  
  signup(username: string, password: string): Observable<any> {
    const signupData = { username, password };
    return this.http.post(`${this.apiUrl}/api/auth/register`, signupData);
  }

  // Guarda tanto el token como el nombre de usuario en el localStorage
  saveSession(token: string, username: string): void {
    localStorage.setItem('jwtToken', token);
    localStorage.setItem('username', username);
  }

  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  // Obtener el nombre de usuario
  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  logout(): void {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('username');
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }
}
