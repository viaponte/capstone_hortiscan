import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { HeaderComponent } from "../../shared/common/header/header.component";
import { UsuarioService } from '../../services/usuarioservice/usuario.service';
import { AuthService } from '../../services/authservice/authservice.service';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { SyncService } from '../../services/syncservice/sync.service';
import { SafeResourceUrl, DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-folder',
  standalone: true,
  imports: [RouterModule, HeaderComponent, CommonModule],
  templateUrl: './folder.component.html',
  styleUrl: './folder.component.scss'
})
export class FolderComponent implements OnInit {
  contenidoCarpeta: string[] = []; // Variable para almacenar el contenido de la carpeta
  imagenesMap: { [key: string]: string } = {}; // Mapa para almacenar las URL de las imágenes
  nombreCarpeta: string = '';  // Variable para almacenar el nombre de la carpeta
  username: string | null = '';  // Variable para almacenar el nombre de usuario
  selectedFile: SafeResourceUrl | null = null;  // Archivo seleccionado para mostrar en el modal

  constructor(private route: ActivatedRoute, private usuarioService: UsuarioService,
    private authService: AuthService, private syncService: SyncService,
    private sanitizer: DomSanitizer) {
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
        })
      )
    );

    forkJoin(imageObservables).subscribe(images => {
      images.forEach(img => {
        this.imagenesMap[img.fileName] = img.imageUrl; // Guardamos la URL en lugar del Blob
      });
    });
  }


  selectedFileExtension: string | null = null;  

  // Método para abrir modal
  openModal(file: string): void {
    const fileExtension = file.split('.').pop()?.toLowerCase();
    this.selectedFileExtension = fileExtension ?? null;  // Guardamos la extensión del archivo o null si no existe
  
    // Si es una imagen
    if (fileExtension === 'jpg' || fileExtension === 'png' || fileExtension === 'jpeg' || fileExtension === 'gif') {
      this.usuarioService.getImagePath(this.nombreCarpeta, file).subscribe(
        (imageBlob) => {
          const fileURL = URL.createObjectURL(imageBlob);  // Crear URL desde Blob
          this.selectedFile = this.sanitizer.bypassSecurityTrustResourceUrl(fileURL);  // Sanitizamos la URL
        },
        (error) => {
          console.error('Error al cargar la imagen:', error);
        }
      );
    }
    // Si es un archivo Word
    else if (fileExtension === 'docx' || fileExtension === 'doc') {
      this.usuarioService.getPdfPath(this.nombreCarpeta, file).subscribe(
        (pdfBlob) => {
          const fileURL = URL.createObjectURL(pdfBlob);  // Crear URL desde Blob
          this.selectedFile = this.sanitizer.bypassSecurityTrustResourceUrl(fileURL);  // Sanitizamos la URL
        },
        (error) => {
          console.error('Error al cargar el PDF:', error);
        }
      );
    } else {
      console.error('Formato de archivo no soportado:', fileExtension);
    }
  }

  // Método que retorna la ruta de la imagen
  getImagePath(fileName: string) {
    return this.imagenesMap[fileName]; // Retorna la imagen pre-cargada
  }

  // Método para eliminar una imagen
  deleteImagen(fileName: string) {
    this.usuarioService.deleteImagen(this.nombreCarpeta, fileName).subscribe(
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