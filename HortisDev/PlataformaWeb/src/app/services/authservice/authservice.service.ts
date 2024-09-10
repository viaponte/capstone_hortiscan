import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiUrl } from '../../enviroment/enviroment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    const loginData = { username, password };
    return this.http.post<{ jwt: string }>(`${apiUrl}/api/auth/login`, loginData);
  }

  signup(username: string, password: string): Observable<any> {
    const signupData = { username, password };
    return this.http.post(`${apiUrl}/api/auth/register`, signupData);
  }

  saveToken(token: string): void {
    localStorage.setItem('jwtToken', token);
  }

  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  logout(): void {
    localStorage.removeItem('jwtToken');
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

}
