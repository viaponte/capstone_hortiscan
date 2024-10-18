import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiUrl } from '../../enviroment/enviroment';
import { AuthService } from '../authservice/authservice.service';

@Injectable({
  providedIn: 'root'
})
export class OnlyofficeService {

  constructor(private http: HttpClient, private authService: AuthService) {
    this.username = this.authService.getUsername();
  }

  username: string | null = null;
  
  getDocument(folderName: string, fileName: string): Observable<any> {
    const request = `${apiUrl}/api/document/edit/${ this.username }/${folderName}/${fileName}`;

    return this.http.get(request);
  }
}
