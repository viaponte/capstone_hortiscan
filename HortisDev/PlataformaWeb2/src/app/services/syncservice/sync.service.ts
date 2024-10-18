import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { apiUrl } from '../../enviroment/enviroment';

@Injectable({
  providedIn: 'root'
})
export class SyncService {

  constructor(private http: HttpClient) { }

  // Método para sincronizar carpetas
  syncCarpetas(): Observable<any> {
    return this.http.post(`${ apiUrl }/sync-carpetas-imagenes`, {}, { responseType: 'text' });

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
