import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Camera, CameraResultType, CameraSource, Photo } from '@capacitor/camera';
import { NavController } from '@ionic/angular';
import { map, forkJoin } from 'rxjs';
import { AuthService } from 'src/app/services/authservice/authservice.service';
import { ReloadService } from 'src/app/services/reloadservice/reload.service';
import { UsuarioService } from 'src/app/services/usuarioservice/usuario.service';

@Component({
  selector: 'app-folder-content',
  templateUrl: './folder-content.page.html',
  styleUrls: ['./folder-content.page.scss'],
})
export class FolderContentPage implements OnInit {

  folderName: string = '';
  contenidoCarpeta: string[] = []; // Variable para almacenar el contenido de la carpeta
  imagenesMap: { [key: string]: string } = {}; // Mapa para almacenar las URL de las imágenes
  selectedFile: string | null = null; // Archivo seleccionado para mostrar en el modal
  username: string | null = '';  // Variable para almacenar el nombre de usuario

  constructor(private route: ActivatedRoute, private navCtrl: NavController, private usuarioService: UsuarioService, private authService: AuthService, private reloadService: ReloadService, private cdr: ChangeDetectorRef) {
    this.username = this.authService.getUsername();
  }

  logout() {
    this.navCtrl.navigateBack('/login'); 
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.folderName = params.get('nombreCarpeta')!;
      if (this.username) {
        this.loadContenidoCarpeta();
      }
    });
  }

  // Método para cargar el contenido de la carpeta desde el backend
  loadContenidoCarpeta() {
    this.usuarioService.getCarpetaContenido(this.username!, this.folderName).subscribe(
      (response) => {
        this.contenidoCarpeta = response;  // Cargar el contenido de la carpeta
        this.loadImages(); // Cargar las imágenes en miniatura
      },
      (error) => {
        console.error('Error al cargar el contenido de la carpeta:', error);
      }
    );
  }

  // Método para cargar las imágenes en miniatura
  loadImages() {
    const imageObservables = this.contenidoCarpeta.map(file =>
      this.usuarioService.getImagePath(this.folderName, file).pipe(
        map(imageUrl => ({ fileName: file, imageUrl }))
      )
    );

    forkJoin(imageObservables).subscribe(images => {
      images.forEach(img => {
        this.imagenesMap[img.fileName] = img.imageUrl;
      });
    });
  }

  // Método para abrir modal
  openModal(file: string): void {
    this.selectedFile = this.imagenesMap[file]; // Usa la URL de imagen pre-cargada
  }

  // Método que retorna la ruta de la imagen
  getImagePath(fileName: string) {
    return this.imagenesMap[fileName]; // Retorna la imagen pre-cargada
  }

  async openCamera() {
    const image = await Camera.getPhoto({
      quality: 90,
      allowEditing: false,
      resultType: CameraResultType.Uri,
      source: CameraSource.Camera
    });
  
    const file = await this.photoToFile(image);
    console.log('Archivo creado: ', file);  // Verifica que el archivo contenga los datos correctos
  
    // Enviar el archivo al backend
    this.usuarioService.uploadImage(file, this.folderName).subscribe(
      (response) => {
        console.log('Imagen subida correctamente: ', response);
        this.loadContenidoCarpeta();
      },
      (error) => {
        console.error('Error al subir imagen: ', error);
      }
    );
  }
  
  // Convertir una imagen Photo a File
  async photoToFile(photo: Photo): Promise<File> {
    const response = await fetch(photo.webPath!);  // Obtener la imagen desde la URL
    const blob = await response.blob();            // Convertirla a Blob
  
    // Crear un archivo a partir del Blob
    const fileName = `${new Date().getTime()}.jpeg`;
    return new File([blob], fileName, {
      type: blob.type,
    });
  }

  handleRefresh(event: any) {
    this.reloadService.handleRefresh(event).then(() => {
      this.cdr.detectChanges();
      this.loadContenidoCarpeta();
    });
  }
}
