import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SyncService {

  apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Método para sincronizar carpetas
  syncCarpetas(): Observable<any> {
    return this.http.post(`${ this.apiUrl }/sync-carpetas-imagenes`, {}, { responseType: 'text' });
  }
  

  // Método para llamar sincronización de carpetas
  initSyncCarpetas() {
    this.syncCarpetas().subscribe(
      (response) => {
        console.log(response);
      },
      (error) => {
        console.error('Error al sincronizar carpetas', error);
      }
    );
  }
}
