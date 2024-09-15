import { Component, OnInit } from '@angular/core';
import { NavController } from '@ionic/angular';

@Component({
  selector: 'app-create-folder',
  templateUrl: './create-folder.page.html',
  styleUrls: ['./create-folder.page.scss'],
})
export class CreateFolderPage implements OnInit {

  constructor(private navCtrl: NavController) {}

  openCreateFolderPage() {
    this.navCtrl.navigateForward('/folder-content'); // Cambia la ruta según tu estructura
  }
  ngOnInit() {
  }

}
