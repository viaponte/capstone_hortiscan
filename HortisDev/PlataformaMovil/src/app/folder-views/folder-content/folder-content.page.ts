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



  // Método para crear el archivo Word
  async createWordFile(text: string): Promise<File> {
    // try {
    //   // Cargar la plantilla .docx desde assets
    //   const templateArrayBuffer = await this.http.get(`assets/templates/template.docx`, { responseType: 'arraybuffer' }).toPromise();
    //   // Verificar si la plantilla se ha cargado correctamente
    //   if (!templateArrayBuffer) {
    //     throw new Error("Error al cargar la plantilla de Word");
    //   }

    //   const zip = new PizZip(templateArrayBuffer);
    //   const doc = new Docxtemplater(zip, { paragraphLoop: true, linebreaks: true });

    //   // Inyecta el texto extraído en la plantilla
    //   doc.setData({ text });

    //   // Renderiza el documento
    //   doc.render();

    //   // Genera el archivo Word en formato Blob
    //   const out = doc.getZip().generate({
    //     type: 'blob',
    //     mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    //   });

    //   // Devuelve un archivo en formato File
    //   return new File([out], `${new Date().getTime()}.docx`, { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
    // } catch (error) {
    //   console.error('Error al crear el archivo Word:', error);
    //   throw error;
    // }
    const zip = new JSZip();

    // Estructura básica de un archivo .docx (Word)
    zip.file("[Content_Types].xml", `
      <? xml version = "1.0" encoding = "UTF-8" standalone = "yes" ?>
        <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types" >
    <Default Extension="rels" ContentType = "application/vnd.openxmlformats-package.relationships+xml" />
    <Default Extension="xml" ContentType = "application/xml" />
    <Override PartName="/word/document.xml" ContentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml" />
    </Types>
    `);

    zip.folder("_rels")?.file(".rels", `
      <? xml version = "1.0" encoding = "UTF-8" standalone = "yes" ?>
        <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships" >
    <Relationship Id="rId1" Type = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target = "word/document.xml" />
    </Relationships>
    `);

    zip.folder("word")?.file("document.xml", `
      <? xml version = "1.0" encoding = "UTF-8" standalone = "yes" ?>
        <w: document xmlns: w = "http://schemas.openxmlformats.org/wordprocessingml/2006/main" >
        <w: body >
    <w: p >
    <w: r >
    <w: t > ${text} </w:t>
    </w:r>
    </w:p>
    </w:body>
    </w:document>
    `);

    zip.folder("word/_rels")?.file("document.xml.rels", `
      <?xml version="1.0" encoding = "UTF-8" standalone = "yes" ?>
      <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships" />
      `);

    // Genera el archivo .docx como un Blob
    const blob = await zip.generateAsync({ type: "blob" });

    // Crea un objeto File a partir del Blob
    return new File([blob], "documento.docx", {
      type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    });
  }

  async sendWordFileToServer(extractedText: string) {
    try {
      const wordFile = await this.createWordFile(extractedText);
      this.usuarioService.uploadWord(wordFile, 'nombreDeCarpeta').subscribe(
        (response) => {
          console.log('Documento Word subido correctamente:', response);
        },
        (error) => {
          console.error('Error al subir documento Word:', error);
        }
      );
    } catch (error) {
      console.error('Error al crear o subir el archivo Word:', error);
    }

  }

  // async openCamera() {
  //   const image = await Camera.getPhoto({
  //     quality: 90,
  //     allowEditing: false,
  //     resultType: CameraResultType.Uri,
  //     source: CameraSource.Camera
  //   });


  //   const file = await this.photoToFile(image);
  //   console.log('Archivo creado: ', file);

  //   // Llama al servicio para procesar la imagen en la API de OCR
  //   this.ocrService.processImage(file).subscribe(
  //     async (response) => {
  //       const extractedText = response.text;
  //       console.log('Texto extraído:', extractedText);

  //       try {
  //         const wordFile = await this.createWordFile(extractedText);
  //         // Aquí puedes cargar el archivo o hacer otras operaciones con él
  //         console.log("Archivo Word creado:", wordFile);
  //       } catch (error) {
  //         console.error("Error al crear el archivo Word:", error);
  //       }
  //     },
  //     (error) => {
  //       console.error('Error al procesar la imagen con la API de OCR:', error);
  //     }
  //   );
  //   // Enviar el archivo al backend
  //   this.usuarioService.uploadImage(file, this.folderName).subscribe(
  //     (response) => {
  //       console.log('Imagen subida correctamente: ', response);
  //       this.loadContenidoCarpeta();
  //     },
  //     (error) => {
  //       console.error('Error al subir imagen: ', error);
  //     }
  //   );

  // }
  async openCamera() {
    const image = await Camera.getPhoto({
      quality: 90,
      allowEditing: false,
      resultType: CameraResultType.Uri,
      source: CameraSource.Camera
    });

    const file = await this.photoToFile(image);
    console.log('Archivo creado: ', file);

    // Enviar la imagen al backend para procesar OCR
    this.ocrService.uploadImage(file).subscribe(
      (response) => {
        console.log('Texto extraído y archivo Word creado:', response);
        this.loadContenidoCarpeta();  // Recargar el contenido si es necesario
      },
      (error) => {
        console.error('Error al procesar la imagen con el backend:', error);
      }
    );
  }


  async scanDocument() {
    try {

      // Obtener la imagen escaneada
      const scannedImage = document.getElementById('scannedImage') as HTMLImageElement;
      // Escanea el documento usando el plugin con una imagen máxima
      const { scannedImages, status } = await DocumentScanner.scanDocument({
        maxNumDocuments: 2
      })

      if (status === ScanDocumentResponseStatus.Success && scannedImages?.length) {
        // Comprobar si el elemento existe antes de establecer la propiedad src
        if (scannedImage) {
          const scannedImageUrl = Capacitor.convertFileSrc(scannedImages[0]);
          scannedImage.src = scannedImageUrl;
          scannedImage.style.display = 'block';

          // Descargar la imagen y convertirla en un objeto Blob
          const response = await fetch(scannedImageUrl);
          const blob = await response.blob();

          // Crear un objeto File con el Blob descargado
          const file = new File([blob], `${new Date().getTime()}.jpeg`, { type: blob.type });
          console.log('Archivo creado a partir del documento escaneado: ', file);

          // Subir la imagen escaneada al backend
          this.usuarioService.uploadImage(file, this.folderName).subscribe(
            (response) => {
              console.log('Imagen escaneada subida correctamente:', response);
              this.loadContenidoCarpeta(); // Recargar el contenido de la carpeta
            },
            (error) => {
              console.error('Error al subir la imagen escaneada:', error);
            }
          );
        } else {
          console.error('Elemento con id "scannedImage" no encontrado en el DOM.');
        }
      } else if (status === ScanDocumentResponseStatus.Cancel) {
        // El usuario salió de la cámara
        alert('El usuario canceló el escaneo del documento');
      }
    } catch (error) {
      // Ocurrió un error durante el escaneo del documento
      alert('Error al escanear documento: ' + error);
    }
  }



  // Método para cargar el contenido de la carpeta desde el backend

  // Convierte la imagen capturada en un objeto File
  async photoToFile(photo: Photo): Promise<File> {
    const response = await fetch(photo.webPath!);  // Accede a la URI de la imagen
    const blob = await response.blob();            // Convierte la URI en un Blob
    return new File([blob], `${new Date().getTime()}.jpeg`, { type: blob.type });  // Convierte el Blob a File
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