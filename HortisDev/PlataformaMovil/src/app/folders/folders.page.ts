import { Component } from '@angular/core';
import { NavController } from '@ionic/angular';

@Component({
  selector: 'app-folders',
  templateUrl: './folders.page.html',
  styleUrls: ['./folders.page.scss'],
})
export class FoldersPage {

  folders = [
    { name:'Maipu'},
    { name:'Pudahuel'},
    { name:'Colina'},
    { name:'Rancagua'}
  ]

  constructor(private navCtrl: NavController) {}

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
