import { Component } from '@angular/core';
import { NavController } from '@ionic/angular';

// Se usa para simular que no hay carpetas
// interface Folder {
//   name: string;
// }

@Component({
  selector: 'app-folders',
  templateUrl: './folders.page.html',
  styleUrls: ['./folders.page.scss'],
})
export class FoldersPage {


  // Se usa para simular que no hay carpetas
  // folders: Folder[] = []; 
  folders = [
    { name: 'Maipu' },
    { name: 'Pudahuel' },
    { name: 'Colina' },
    { name: 'Rancagua' },
    { name: 'Maipu' },
    { name: 'Pudahuel' },
    { name: 'Colina' },
    { name: 'Rancagua' }
  ]
  constructor(private navCtrl: NavController) { }

  openFolder(folderName: string) {
    this.navCtrl.navigateForward(`/folder-content/${folderName}`);
  }
  logout() {
    this.navCtrl.navigateBack('/login'); // Navega a la página de login
  }
  createFolder() {
    this.navCtrl.navigateForward('/create-folder');
  }
}
