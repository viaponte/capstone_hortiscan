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
import { DocumentScanner } from 'capacitor-document-scanner';
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

  // async scanDocument() {
  //   try {
  //     // Asegúrate de que el plugin esté correctamente invocado para escanear el documento
  //     const { scannedImages } = await DocumentScanner.scanDocument();

  //     if (scannedImages && scannedImages.length > 0) {
  //       const scannedImageUrl = Capacitor.convertFileSrc(scannedImages[0]);

  //       // Mostrar la imagen escaneada en la UI (opcional)
  //       const scannedImage = document.getElementById('scannedImage') as HTMLImageElement;
  //       scannedImage.src = scannedImageUrl;

  //       // Descargar la imagen escaneada y convertirla en un objeto Blob
  //       const response = await fetch(scannedImageUrl);
  //       const blob = await response.blob();

  //       // Crear un objeto File con el Blob descargado
  //       const file = await this.photoToFile(blob);
  //       console.log('Archivo creado: ', file);

  //       // Subir la imagen escaneada al backend usando el servicio de usuario
  //       this.usuarioService.uploadImage(file, this.folderName).subscribe(
  //         (response) => {
  //           console.log('Imagen escaneada subida correctamente:', response);
  //           this.loadContenidoCarpeta(); // Recargar el contenido de la carpeta
  //         },
  //         (error) => {
  //           console.error('Error al subir la imagen escaneada:', error);
  //         }
  //       );
  //     } else {
  //       console.log('No se escanearon imágenes');
  //     }
  //   } catch (error) {
  //     console.error('Error al escanear documento:', error);
  //   }
  // }

  // Método para cargar el contenido de la carpeta desde el backend
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

  // Convierte la imagen capturada en un objeto File
  async photoToFile(photo: Photo): Promise<File> {
    const response = await fetch(photo.webPath!);
    const blob = await response.blob();
    return new File([blob], `${new Date().getTime()}.jpeg`, { type: blob.type });
  }
  
  // Método que convierte una imagen a un archivo (similar a openCamera)
  // async photoToFile(imageBlob: Blob): Promise<File> {
  //   const fileName = `${new Date().getTime()}.jpeg`;  // Puedes ajustar el nombre y la extensión según tus necesidades
  //   const file = new File([imageBlob], fileName, { type: imageBlob.type });
  //   return file;
  // }

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
  async openCamera() {
    const image = await Camera.getPhoto({
      quality: 90,
      allowEditing: false,
      resultType: CameraResultType.Uri,
      source: CameraSource.Camera
    });


    const file = await this.photoToFile(image);
    console.log('Archivo creado: ', file);

    // Llama al servicio para procesar la imagen en la API de OCR
    // this.ocrService.processImage(file).subscribe(
    //   (response) => {
    //     const extractedText = response.text;
    //     console.log('Texto extraído:', extractedText);

    //     // Crear el archivo Word con el texto extraído
    //     this.createWordFile(extractedText).then((wordFile) => {
    //       this.usuarioService.uploadWord(wordFile, this.folderName).subscribe(
    //         (response) => {
    //           console.log('Documento Word subido correctamente:', response);
    //           this.loadContenidoCarpeta();
    //         },
    //         (error) => {
    //           console.error('Error al subir documento Word:', error);
    //         }
    //       );
    //     });
    //   },
    //   (error) => {
    //     console.error('Error al procesar la imagen con la API de OCR:', error);
    //   }
    // );
    // Llama al servicio para procesar la imagen en la API de OCR
    this.ocrService.processImage(file).subscribe(
      async (response) => {
        const extractedText = response.text;
        console.log('Texto extraído:', extractedText);

        try {
          const wordFile = await this.createWordFile(extractedText);
          // Aquí puedes cargar el archivo o hacer otras operaciones con él
          console.log("Archivo Word creado:", wordFile);
        } catch (error) {
          console.error("Error al crear el archivo Word:", error);
        }
      },
      (error) => {
        console.error('Error al procesar la imagen con la API de OCR:', error);
      }
    );
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