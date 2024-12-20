import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NavController } from '@ionic/angular';
import { forkJoin, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { AuthService } from 'src/app/services/authservice/authservice.service';
import { ReloadService } from 'src/app/services/reloadservice/reload.service';
import { UsuarioService } from 'src/app/services/usuarioservice/usuario.service';
import { HttpClient } from '@angular/common/http';
import {
  DocumentScanner,
  ScanDocumentResponseStatus,
} from 'capacitor-document-scanner';
import { Capacitor } from '@capacitor/core';
<<<<<<< HEAD
=======
import { NotificacionService } from 'src/app/services/notificacionservice/notificacion.service'; // Asegúrate de que la ruta sea correcta
import { NotificacionDTO } from '../../models/NotificacionDTO';
<<<<<<< HEAD
>>>>>>> develop

=======
import { Filesystem, Directory } from '@capacitor/filesystem';
import { FileOpener } from '@capacitor-community/file-opener';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
>>>>>>> cambios_qa

@Component({
  selector: 'app-folder-content',
  templateUrl: './folder-content.page.html',
  styleUrls: ['./folder-content.page.scss'],
})
export class FolderContentPage implements OnInit {
  extractedText: string | null = null;
<<<<<<< HEAD

=======
>>>>>>> develop
  folderName: string = '';
  contenidoCarpeta: string[] = []; // Variable para almacenar el contenido de la carpeta
  imagenesMap: { [key: string]: string } = {}; // Mapa para almacenar las URL de las imágenes
  selectedFile: string | null = null; // Archivo seleccionado para mostrar en el modal
  username: string | null = ''; // Variable para almacenar el nombre de usuario
  selectedDocxContent: string | null = null; // Variable para almacenar el contenido del archivo .docx

  pdfSrc: string | ArrayBuffer | Blob | Uint8Array | URL | { range: any; } | null = null;
  selectedFileName: string | null = null;
  isImage: boolean = false;
  isModalOpen: boolean = false;

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private navCtrl: NavController,
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private reloadService: ReloadService,
<<<<<<< HEAD
    private cdr: ChangeDetectorRef
=======
    private cdr: ChangeDetectorRef,
<<<<<<< HEAD
    private notificacionService: NotificacionService // Inyección del servicio de notificaciones
>>>>>>> develop
=======
    private notificacionService: NotificacionService,
    private sanitizer: DomSanitizer
>>>>>>> cambios_qa
  ) {
    this.username = this.authService.getUsername();
  }

  logout() {
    this.navCtrl.navigateBack('/login');
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.folderName = params.get('nombreCarpeta')!;
      if (this.username) {
        this.loadContenidoCarpeta();
      }
    });
  }

  // Método para cargar las imágenes en miniatura
  loadImages() {
    const imageObservables = this.contenidoCarpeta.map((file) =>
      this.usuarioService
        .getImagePath(this.folderName, file)
        .pipe(map((imageUrl) => ({ fileName: file, imageUrl })))
    );

    forkJoin(imageObservables).subscribe((images) => {
      images.forEach((img) => {
        this.imagenesMap[img.fileName] = img.imageUrl;
      });
    });
  }

  // Método para abrir modal
  async openModal(file: string): Promise<void> {
    this.selectedFileName = file;

    const fileExtension = file.split('.').pop()?.toLowerCase();

    if (fileExtension === 'jpg' || fileExtension === 'png' || fileExtension === 'jpeg' || fileExtension === 'gif') {
      this.selectedFile = this.imagenesMap[file];
      this.isImage = true;
      this.isModalOpen = true;
    } else if (fileExtension === 'docx' || fileExtension === 'doc') {
      try {
        const timestamp = new Date().getTime();
        const pdfBlob = await this.usuarioService.getPdfPath(this.folderName, file, timestamp).toPromise();

        if (!pdfBlob) {
          console.error('No se obtuvo el archivo PDF desde el servidor.');
          return;
        }

        this.pdfSrc = pdfBlob;
        this.isImage = false;
        this.isModalOpen = true;
      } catch (error) {
        console.error('Error al cargar el PDF:', error);
      }
    }
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedFile = null;
    this.pdfSrc = null;
    this.selectedFileName = null;
  }

  async scanDocument() {
    try {
      // Escanear el documento permitiendo múltiples imágenes
      const { scannedImages, status } = await DocumentScanner.scanDocument({
        maxNumDocuments: 4,
        letUserAdjustCrop: true, // Permitir ajuste de recorte
        croppedImageQuality: 100,
      });

      if (
        status === ScanDocumentResponseStatus.Success &&
        scannedImages?.length
      ) {
        console.log(`Se escanearon ${scannedImages.length} imágenes`);

        // Iterar sobre todas las imágenes escaneadas
        for (const imagePath of scannedImages) {
          const scannedImageUrl = Capacitor.convertFileSrc(imagePath);
          console.log('URL de la imagen escaneada:', scannedImageUrl);

          // Mostrar la imagen en la UI si el elemento existe
          const scannedImage = document.getElementById(
            'scannedImage'
          ) as HTMLImageElement;
          if (scannedImage) {
            scannedImage.src = scannedImageUrl;
            scannedImage.style.display = 'block';
          } else {
            console.error(
              'Elemento con id "scannedImage" no encontrado en el DOM.'
            );
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
          const file = new File([blob], `${new Date().getTime()}.jpeg`, {
            type: blob.type,
          });
          console.log('Archivo creado:', file);

          // Subir cada imagen al backend
          await this.uploadImageToBackend(file);
<<<<<<< HEAD
=======

          // Crear y enviar la notificación después de guardar la imagen
          const mensajeNotificacion = `Imagen escaneada guardada en la carpeta "${this.folderName}" exitosamente.`;
          const notificacionDTO: NotificacionDTO = {
            idNotificacion: 0,
            mensajeNotificacion: mensajeNotificacion,
            fechaNotificacion: new Date().toISOString(),
          };

          this.notificacionService.crearNotificacion(notificacionDTO).subscribe(
            (notificacionResponse) => {
              console.log('Notificación creada:', notificacionResponse);
            },
            (error) => {
              console.error('Error al crear la notificación:', error);
            }
          );
>>>>>>> develop
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
      const response = await this.usuarioService
        .uploadImage(file, this.folderName)
        .toPromise();
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

<<<<<<< HEAD
  // Método para eliminar una imagen
=======
  loadContenidoCarpeta() {
    const username = this.authService.getUsername();
    if (username) {
      this.usuarioService
        .getCarpetaContenido(username, this.folderName)
        .subscribe(
          (response) => {
            this.contenidoCarpeta = response;
            this.loadImages();
          },
          (error) => {
            console.error('Error al cargar el contenido de la carpeta:', error);
          }
        );
    } else {
      console.error('No se ha encontrado el nombre de usuario.');
    }
  }

  // Método para eliminar una imagen o un documento
>>>>>>> develop
  deleteImagen(fileName: string) {
    const fileExtension = fileName.split('.').pop()?.toLowerCase();

    // Para imágenes
    if (
      fileExtension === 'jpg' ||
      fileExtension === 'png' ||
      fileExtension === 'jpeg' ||
      fileExtension === 'gif'
    ) {
      this.usuarioService.deleteImagen(this.folderName, fileName).subscribe(
        (response) => {
          console.log('Imagen eliminada: ', response);
          // Actualizar el estado de contenidoCarpeta directamente, eliminando la imagen
          this.contenidoCarpeta = this.contenidoCarpeta.filter(
            (file) => file !== fileName
          );
          this.showNotification(
            `Imagen "${fileName}" eliminada de la carpeta "${this.folderName}" exitosamente.`
          );
        },
        (error) => {
          console.error('Error al eliminar la imagen: ', error);
        }
      );
    } else if (fileExtension === 'docx' || fileExtension === 'doc') {
      // Para formularios
      this.usuarioService.deleteFormulario(this.folderName, fileName).subscribe(
        (response) => {
          console.log('Formulario eliminado: ', response);
          // Actualizar el estado de contenidoCarpeta directamente, eliminando el formulario
          this.contenidoCarpeta = this.contenidoCarpeta.filter(
            (file) => file !== fileName
          );
          this.showNotification(
            `Formulario "${fileName}" eliminado de la carpeta "${this.folderName}" exitosamente.`
          );
        },
        (error) => {
          console.error('Error al eliminar el formulario: ', error);
        }
      );
    }
  }

  // Método para mostrar notificación
  showNotification(message: string) {
    const notificationDTO: NotificacionDTO = {
      idNotificacion: 0,
      mensajeNotificacion: message,
      fechaNotificacion: new Date().toISOString(),
    };

    this.notificacionService.crearNotificacion(notificationDTO).subscribe(
      (response) => {
<<<<<<< HEAD
        console.log('Imagen eliminada: ', response);
        this.loadContenidoCarpeta();
=======
        console.log('Notificación de eliminación creada:', response);
>>>>>>> develop
      },
      (error) => {
        console.error('Error al crear la notificación de eliminación:', error);
      }
    );
  }
<<<<<<< HEAD
<<<<<<< HEAD
=======


>>>>>>> develop
}
=======
}
>>>>>>> cambios_qa
