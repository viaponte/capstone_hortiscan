import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Camera, CameraResultType, CameraSource } from '@capacitor/camera';
import { NavController } from '@ionic/angular';

@Component({
  selector: 'app-folder-content',
  templateUrl: './folder-content.page.html',
  styleUrls: ['./folder-content.page.scss'],
})
export class FolderContentPage implements OnInit {

  folderName: string = '';
  images: Array<{ src: string, name: string }> = [];

  constructor(private route: ActivatedRoute, private navCtrl: NavController) {}

  logout() {
    this.navCtrl.navigateBack('/login'); // Navega a la página de login
  }

  ngOnInit() {
    this.folderName = this.route.snapshot.paramMap.get('folderName')!;
    this.loadFolderContent();
    // Cargar las imágenes correspondientes a la carpeta seleccionada.
  }

  loadFolderContent() {
    // Simulación de la carga del contenido de la carpeta.
    // Debes reemplazar esto con la lógica para cargar los archivos/imágenes reales.
    if (this.folderName === 'Maipu') {
      this.images = [
        { src: 'assets/images/form.png', name: 'Maipu - 1' },
        { src: 'assets/images/form.png', name: 'Maipu - 2' },
        { src: 'assets/images/form.png', name: 'Maipu - 3' },
        { src: 'assets/images/form.png', name: 'Maipu - 4' }
      ];
    }
    // Añadir más casos según las carpetas disponibles.
  }
  async openCamera() {
    const image = await Camera.getPhoto({
      quality: 90,
      allowEditing: false,
      resultType: CameraResultType.Uri,
      source: CameraSource.Camera
    });

    // Aquí puedes hacer algo con la imagen capturada, como guardarla o procesarla.
    console.log('Imagen capturada: ', image);
  }
}
