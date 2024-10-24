import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Camera, CameraResultType, CameraSource, Photo } from '@capacitor/camera';
import { NavController } from '@ionic/angular';
import { map, forkJoin } from 'rxjs';
import { AuthService } from 'src/app/services/authservice/authservice.service';
import { ReloadService } from 'src/app/services/reloadservice/reload.service';
import { UsuarioService } from 'src/app/services/usuarioservice/usuario.service';
import * as PizZip from 'pizzip';
import * as Docxtemplater from 'docxtemplater';
import { OcrService } from '../../services/ocrservice/ocr.service';
import { HttpClient } from '@angular/common/http';
import * as JSZip from 'jszip';
import { DocumentScanner, ScanDocumentResponseStatus } from 'capacitor-document-scanner';
import { Capacitor } from '@capacitor/core';


@Component({
  selector: 'app-folder-content',
  templateUrl: './folder-content.page.html',
  styleUrls: ['./folder-content.page.scss'],
})
export class FolderContentPage implements OnInit {
  extractedText: string | null = null;

  folderName: string = '';
  contenidoCarpeta: string[] = []; // Variable para almacenar el contenido de la carpeta
  imagenesMap: { [key: string]: string } = {}; // Mapa para almacenar las URL de las imágenes
  selectedFile: string | null = null; // Archivo seleccionado para mostrar en el modal
  username: string | null = '';  // Variable para almacenar el nombre de usuario

  constructor(
    private http: HttpClient,
    private ocrService: OcrService,
    private route: ActivatedRoute,
    private navCtrl: NavController,
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private reloadService: ReloadService,
    private cdr: ChangeDetectorRef
  ) {
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

  loadContenidoCarpeta() {
    this.usuarioService.getCarpetaContenido(this.username!, this.folderName).subscribe(
      (response) => {
        this.contenidoCarpeta = response;
        this.loadImages();
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
    this.selectedFile = this.imagenesMap[file];
  }


  async scanDocument() {
    try {
      // Escanear el documento permitiendo múltiples imágenes
      const { scannedImages, status } = await DocumentScanner.scanDocument({
        maxNumDocuments: 4,
        letUserAdjustCrop: true, // Permitir ajuste de recorte
        croppedImageQuality: 100,
      });

      if (status === ScanDocumentResponseStatus.Success && scannedImages?.length) {
        console.log(`Se escanearon ${scannedImages.length} imágenes`);

        // Iterar sobre todas las imágenes escaneadas
        for (const imagePath of scannedImages) {
          const scannedImageUrl = Capacitor.convertFileSrc(imagePath);
          console.log('URL de la imagen escaneada:', scannedImageUrl);

          // Mostrar la imagen en la UI si el elemento existe
          const scannedImage = document.getElementById('scannedImage') as HTMLImageElement;
          if (scannedImage) {
            scannedImage.src = scannedImageUrl;
            scannedImage.style.display = 'block';
          } else {
            console.error('Elemento con id "scannedImage" no encontrado en el DOM.');
          }

          // Descargar la imagen y convertirla a Blob
          const response = await fetch(scannedImageUrl);
          const blob = await response.blob();

          // Verificar que el Blob no esté vacío
          if (!blob.size) {
            console.error('El Blob está vacío. Verifica la URL de la imagen.');
            continue; // Saltar a la siguiente imagen si hay un problema
          }

          // Crear un objeto File con el Blob
          const file = new File([blob], `${new Date().getTime()}.jpeg`, { type: blob.type });
          console.log('Archivo creado:', file);

          // Subir cada imagen al backend
          await this.uploadImageToBackend(file);
        }
      } else if (status === ScanDocumentResponseStatus.Cancel) {
        alert('El usuario canceló el escaneo del documento');
      }
    } catch (error) {
      console.error('Error al escanear documento:', error);
      alert('Error al escanear documento: ' + error);
    }
  }

  // Método para subir la imagen al backend
  private async uploadImageToBackend(file: File) {
    try {
      const response = await this.usuarioService.uploadImage(file, this.folderName).toPromise();
      console.log('Imagen escaneada subida correctamente:', response);
      this.loadContenidoCarpeta(); // Recargar contenido
    } catch (error) {
      console.error('Error al subir la imagen:', error);
      alert('No se pudo subir la imagen. Verifica el backend.');
    }
  }

  handleRefresh(event: any) {
    this.reloadService.handleRefresh(event).then(() => {
      this.cdr.detectChanges();
      this.loadContenidoCarpeta();
    });
  }

  // Método que retorna la ruta de la imagen
  getImagePath(fileName: string) {
    return this.imagenesMap[fileName]; // Retorna la URL pre-cargada de la imagen si existe
  }

  // Método para eliminar una imagen
  deleteImagen(fileName: string) {
    this.usuarioService.deleteImagen(this.folderName, fileName).subscribe(
      (response) => {
        console.log('Imagen eliminada: ', response);
        this.loadContenidoCarpeta();
      },
      (error) => {
        console.error('Error al eliminar la imagen: ', error);
      }
    );
  }
}