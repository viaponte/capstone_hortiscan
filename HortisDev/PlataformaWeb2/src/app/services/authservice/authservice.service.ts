import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiUrl } from '../../enviroment/enviroment';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    const loginData = { username, password };
    return this.http.post<{ jwt: string }>(`${apiUrl}/api/auth/login`, loginData).pipe(
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
    return this.http.post(`${apiUrl}/api/auth/register`, signupData);
  }

  // Guarda tanto el token como el username en el localStorage
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
