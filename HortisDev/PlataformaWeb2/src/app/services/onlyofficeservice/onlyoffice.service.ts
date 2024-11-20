import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
<<<<<<< HEAD
import { Observable } from 'rxjs';
=======
import { catchError, map, Observable, throwError } from 'rxjs';
>>>>>>> develop
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
<<<<<<< HEAD
=======

  getFileLastModified(nombreCarpeta: string, fileName: string): Observable<number> {
    const request = `${apiUrl}/api/document/lastModified/${this.username}/${nombreCarpeta}/${fileName}`;
    return this.http.get<{ lastModified: number }>(request).pipe(
      map(response => response.lastModified),
      catchError(error => {
        console.error('Error al obtener la fecha de modificación:', error);
        return throwError(() => new Error('Error al obtener la fecha de modificación.'));
      })
    );
  }
>>>>>>> develop
}
