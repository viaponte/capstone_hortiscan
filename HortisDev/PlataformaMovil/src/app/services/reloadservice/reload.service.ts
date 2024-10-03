import { Injectable } from '@angular/core';
import { SyncService } from '../syncservice/sync.service';

@Injectable({
  providedIn: 'root'
})
export class ReloadService {

  constructor(private syncService: SyncService) { }

  handleRefresh(event: any): Promise<void> {
    return new Promise((resolve) => {
      setTimeout(() => {
        this.syncService.initSyncCarpetas().then(() => {
          event.target.complete();
          resolve();  // Resolvemos la promesa cuando la sincronización termine
        }).catch((error) => {
          console.error('Error al refrescar:', error);
          event.target.complete();
          resolve();  // También resolvemos aunque haya un error, para cerrar el refresher
        });
      }, 1000);
    });
  }
}
