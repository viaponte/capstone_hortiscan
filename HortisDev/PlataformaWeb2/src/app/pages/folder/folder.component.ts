import { Component, OnInit, OnDestroy,EventEmitter, Input, Output } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { HeaderComponent } from "../../shared/common/header/header.component";
import { UsuarioService } from '../../services/usuarioservice/usuario.service';
import { AuthService } from '../../services/authservice/authservice.service';
import { CommonModule } from '@angular/common';
import { forkJoin, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { SyncService } from '../../services/syncservice/sync.service';
import { SafeResourceUrl, DomSanitizer } from '@angular/platform-browser';
<<<<<<< HEAD
=======
import { NotificacionService } from '../../services/notificacionservice/notificacion.service'; // Asegúrate de que la ruta sea correcta
import { NotificacionDTO } from '../../models/NotificacionDTO';
import { NotificacionhelperService } from '../../services/notificacionHelperService/notificacionhelper.service';
import { CarouselComponent } from '../../shared/common/carousel/carousel.component';
>>>>>>> develop

@Component({
  selector: 'app-folder',
  standalone: true,
  imports: [RouterModule, HeaderComponent, CommonModule, CarouselComponent],
  templateUrl: './folder.component.html',
  styleUrl: './folder.component.scss'
})
export class FolderComponent implements OnInit, OnDestroy {
  @Input() folderName?: string;
  @Output() backClicked = new EventEmitter<void>();

  contenidoCarpeta: string[] = []; // Variable para almacenar el contenido de la carpeta
  imagenesMap: { [key: string]: string } = {}; // Mapa para almacenar las URL de las imágenes
  nombreCarpeta: string = '';  // Variable para almacenar el nombre de la carpeta
  username: string | null = '';  // Variable para almacenar el nombre de usuario
  selectedFile: SafeResourceUrl | null = null;  // Archivo seleccionado para mostrar en el modal
  showEdit: boolean = false;
<<<<<<< HEAD
=======
  isLoading: boolean = false; // Controlar el spinner de carga
>>>>>>> develop

  currentObjectUrl: string | null = null; // Nueva propiedad para almacenar el Object URL original

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private syncService: SyncService,
<<<<<<< HEAD
    private sanitizer: DomSanitizer
=======
    private sanitizer: DomSanitizer,
    private notificacionHelperService: NotificacionhelperService
>>>>>>> develop
  ) {
    this.username = this.authService.getUsername();
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.nombreCarpeta = params.get('nombreCarpeta')!;
      if (this.username) {
        this.loadContenidoCarpeta();
      }
    });
  }

  ngOnDestroy(): void {
    // Revocar el Object URL si existe al destruir el componente
    if (this.currentObjectUrl) {
      URL.revokeObjectURL(this.currentObjectUrl);
      this.currentObjectUrl = null;
    }
  }

  // Método para cargar el contenido de la carpeta desde el backend
  loadContenidoCarpeta() {
    this.usuarioService.getCarpetaContenido(this.username!, this.nombreCarpeta).subscribe(
      (response) => {
        this.contenidoCarpeta = response;  // Cargar el contenido de la carpeta
        this.syncService.initSyncCarpetas();
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
      this.usuarioService.getImagePath(this.nombreCarpeta, file).pipe(
        map((imageBlob) => {
          // Convertir el Blob en una URL antes de almacenarlo
          const imageUrl = URL.createObjectURL(imageBlob);
          return { fileName: file, imageUrl };
        }),
        catchError(error => {
          console.error(`Error al cargar la imagen ${file}:`, error);
          return throwError(() => error);
        })
      )
    );

    forkJoin(imageObservables).subscribe(images => {
      images.forEach(img => {
        this.imagenesMap[img.fileName] = img.imageUrl; // Guardamos la URL en lugar del Blob
      });
    }, error => {
      console.error('Error al cargar las imágenes:', error);
    });
  }

  selectedFileExtension: string | null = null;
  selectedFileName: string | null = null;  // Para almacenar el nombre del archivo seleccionado

  // Método para abrir modal
  openModal(file: string): void {
    this.selectedFileName = file;  // Guardamos el nombre del archivo seleccionado
<<<<<<< HEAD
=======
    this.isLoading = true; // Mostrar el spinner de carga
>>>>>>> develop

    const fileExtension = file.split('.').pop()?.toLowerCase();
    this.selectedFileExtension = fileExtension ?? null;  // Guardamos la extensión del archivo o null si no existe

    // Antes de crear un nuevo URL, revoca el anterior si existe
    if (this.currentObjectUrl) {
      URL.revokeObjectURL(this.currentObjectUrl);
      this.currentObjectUrl = null;
    }

    // Si es una imagen
    if (fileExtension === 'jpg' || fileExtension === 'png' || fileExtension === 'jpeg' || fileExtension === 'gif') {
      this.usuarioService.getImagePath(this.nombreCarpeta, file).subscribe(
        (imageBlob) => {
          const fileURL = URL.createObjectURL(imageBlob);  // Crear URL desde Blob
          this.currentObjectUrl = fileURL; // Almacenar el Object URL original
          this.selectedFile = this.sanitizer.bypassSecurityTrustResourceUrl(fileURL);  // Sanitizamos la URL
<<<<<<< HEAD
        },
        (error) => {
          console.error('Error al cargar la imagen:', error);
=======
          this.isLoading = false; // Ocultar el spinner
        },
        (error) => {
          console.error('Error al cargar la imagen:', error);
          this.isLoading = false; // Ocultar el spinner
>>>>>>> develop
        }
      );
    }
    // Si es un archivo Word
    else if (fileExtension === 'docx' || fileExtension === 'doc') {
<<<<<<< HEAD
      this.usuarioService.getPdfPath(this.nombreCarpeta, file).subscribe(
        (pdfBlob) => {
          const fileURL = URL.createObjectURL(pdfBlob);  // Crear URL desde Blob
          this.currentObjectUrl = fileURL; // Almacenar el Object URL original
          this.selectedFile = this.sanitizer.bypassSecurityTrustResourceUrl(fileURL);  // Sanitizamos la URL
          this.showEdit = true;
        },
        (error) => {
          console.error('Error al cargar el PDF:', error);
        }
      );
    } else {
      console.error('Formato de archivo no soportado:', fileExtension);
=======
      // Antes de asignar selectedFile, asignar null
      this.selectedFile = null;

      // Obtener la fecha actual como marca de tiempo
      const timestamp = new Date().getTime();

      this.usuarioService.getPdfPath(this.nombreCarpeta, file, timestamp).subscribe(
        (pdfBlob) => {
          const fileURL = URL.createObjectURL(pdfBlob);
          this.currentObjectUrl = fileURL;
          setTimeout(() => {
            this.selectedFile = this.sanitizer.bypassSecurityTrustResourceUrl(fileURL);
            this.showEdit = true;
            this.isLoading = false; // Ocultar el spinner
          }, 0);
        },
        (error) => {
          console.error('Error al cargar el PDF:', error);
          this.isLoading = false; // Ocultar el spinner
        }
      );
>>>>>>> develop
    }
  }

  // Método que retorna la ruta de la imagen
  getImagePath(fileName: string) {
    return this.imagenesMap[fileName]; // Retorna la imagen pre-cargada
  }

  // Método para eliminar una imagen
  deleteImagen(fileName: string) {
<<<<<<< HEAD
    this.usuarioService.deleteImagen(this.nombreCarpeta, fileName).subscribe(
      (response) => {
        console.log('Imagen eliminada: ', response);
        this.loadContenidoCarpeta();
      },
      (error) => {
        console.error('Error al eliminar la imagen: ', error);
      }
    );
=======
    const fileExtension = fileName.split('.').pop()?.toLowerCase();

    if(fileExtension === 'jpg' || fileExtension === 'png' || fileExtension === 'jpeg' || fileExtension === 'gif') {
      this.usuarioService.deleteImagen(this.nombreCarpeta, fileName).subscribe(
        (response) => {
          console.log('Imagen eliminada: ', response);
          this.loadContenidoCarpeta();
          this.syncService.initSyncCarpetas();
  
          // Crear y enviar la notificación después de eliminar la imagen
          const mensajeNotificacion = `Imagen "${fileName}" eliminada de la carpeta "${this.nombreCarpeta}" exitosamente.`;
          this.notificacionHelperService.crearNotificacion(mensajeNotificacion);
        },
        (error) => {
          console.error('Error al eliminar la imagen: ', error);
        }
      );
    } else if (fileExtension === 'docx' || fileExtension === 'doc') {
      const formularioNombre = fileName;

      this.usuarioService.deleteFormulario(this.nombreCarpeta, formularioNombre).subscribe(
        (response) => {
          this.loadContenidoCarpeta();
          this.syncService.initSyncCarpetas();
  
          // Crear y enviar la notificación después de eliminar la imagen
          const mensajeNotificacion = `Formulario "${fileName}" eliminado de la carpeta "${this.nombreCarpeta}" exitosamente.`;
          this.notificacionHelperService.crearNotificacion(mensajeNotificacion);
          
        },
        (error) => {
          console.error('Error al eliminar el formulario: ', error);
          this.loadContenidoCarpeta();
          this.syncService.initSyncCarpetas();
  
          // Crear y enviar la notificación después de eliminar la imagen
          const mensajeNotificacion = `Formulario "${fileName}" eliminado de la carpeta "${this.nombreCarpeta}" exitosamente.`;
          this.notificacionHelperService.crearNotificacion(mensajeNotificacion);
        }
      );
    }
>>>>>>> develop
  }

  editDocument(file: any) {
    // Cerrar el modal manualmente seleccionando el modal abierto
    const modals = document.querySelectorAll('.modal.show');
    modals.forEach(modal => {
      (modal as any).classList.remove('show');  // Removemos la clase 'show' para cerrar el modal
      (modal as any).setAttribute('aria-hidden', 'true');
      document.body.classList.remove('modal-open');  // Remover la clase modal-open del body
      const backdrop = document.querySelector('.modal-backdrop');
      if (backdrop) {
        backdrop.remove();  // Eliminamos el backdrop del DOM
      }
    });

    // Navegar al componente onlyoffice-editor
    this.router.navigate(['/editor'], { queryParams: { folderName: this.nombreCarpeta, fileName: file } });
  }

  goBack(): void {
    if (this.folderName) {
      this.backClicked.emit();
    } else {
      this.router.navigate(['/menu']);
    }
  }
<<<<<<< HEAD
}
=======
}
>>>>>>> develop
