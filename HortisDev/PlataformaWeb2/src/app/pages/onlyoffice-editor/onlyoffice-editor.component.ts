import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { apiUrl } from '../../enviroment/enviroment';
import { HeaderComponent } from "../../shared/common/header/header.component";
import { OnlyofficeService } from '../../services/onlyofficeservice/onlyoffice.service';
<<<<<<< HEAD
import { ActivatedRoute } from '@angular/router';
=======
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SpinnerService } from '../../services/spinnerservice/spinner.service';
>>>>>>> develop

@Component({
  selector: 'app-onlyoffice-editor',
  standalone: true,
<<<<<<< HEAD
  imports: [HeaderComponent],
=======
  imports: [HeaderComponent, CommonModule],
>>>>>>> develop
  templateUrl: './onlyoffice-editor.component.html',
  styleUrl: './onlyoffice-editor.component.scss'
})
export class OnlyofficeEditorComponent implements OnInit {
  config: any;
  folderName!: string;
  fileName!: string;
<<<<<<< HEAD

  constructor(private http: HttpClient, private route: ActivatedRoute, private officeService: OnlyofficeService) { }
=======
  docEditor: any;
  isSaving: boolean = false;
  username: string | null = '';

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private officeService: OnlyofficeService,
    private router: Router,
    private onlyofficeService: OnlyofficeService,
    private spinnerService: SpinnerService
  ) { }
>>>>>>> develop

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.folderName = params['folderName'];
      this.fileName = params['fileName'];
<<<<<<< HEAD

      console.log(this.folderName, this.fileName)
      setTimeout(() => {
        if(this.folderName, this.fileName) {
          this.getDocument();
        }
      }, 0);
=======
      this.username = localStorage.getItem('username');

      if (this.folderName && this.fileName) {
        this.getDocument();
      }
>>>>>>> develop
    });
  }

  getDocument() {
<<<<<<< HEAD
    this.officeService.getDocument(this.folderName, this.fileName).subscribe(
      (response: any) => {
        this.config = response.config;
        this.initEditor();
      }
    )
  }

  initEditor() {
    const docEditor = new DocsAPI.DocEditor('placeholder', this.config);
  }

}


=======
    // Mostrar el spinner de carga
    this.spinnerService.show();

    this.officeService.getDocument(this.folderName, this.fileName).subscribe(
      (response: any) => {
        this.config = response.config;
        this.spinnerService.hide(); // Ocultar el spinner
        this.initEditor();
      },
      (error) => {
        console.error('Error al obtener el documento: ', error);
        this.spinnerService.hide(); // Ocultar el spinner en caso de error
      }
    );
  }

  initEditor() {
    const config = this.config;

    config.editorConfig.events = {
      'onRequestClose': () => {
        this.onEditorRequestClose();
        return false; // Prevenir el cierre automático
      },
      'onSave': () => {
        // Se ha iniciado el guardado
        this.isSaving = true;
      },
      'onReady': () => {
        // Obtener referencia al editor una vez que esté listo
        this.docEditor = new DocsAPI.DocEditor('placeholder', config);
      },
      'onError': (error: any) => {
        // Manejar errores si es necesario
        console.error('Error en OnlyOffice:', error);
      },
    };

    const docEditor = new DocsAPI.DocEditor('placeholder', config);
  }

  onEditorRequestClose() {
    if (this.docEditor) {
      this.isSaving = true; // Mostrar indicador de carga
      this.spinnerService.show(); // Mostrar spinner
      this.docEditor.save();

      // Obtener el tiempo actual
      const initialTime = Date.now();

      // Comenzar a verificar si el archivo ha sido actualizado
      this.checkFileUpdated(initialTime);
    } else {
      this.navigateBack();
    }
  }

  checkFileUpdated(initialTime: number) {
    // Llamar al servicio para obtener la última fecha de modificación del archivo
    this.onlyofficeService.getFileLastModified(this.folderName, this.fileName).subscribe(
      (lastModified) => {
        if (lastModified >= initialTime) {
          // El archivo ha sido actualizado
          this.isSaving = false; // Ocultar indicador
          this.spinnerService.hide(); // Ocultar spinner
          this.navigateBack();
        } else {
          // Esperar y volver a comprobar
          setTimeout(() => {
            this.checkFileUpdated(initialTime);
          }, 1000); // Volver a comprobar en 1 segundo
        }
      },
      (error) => {
        console.error('Error al verificar la actualización del archivo:', error);
        this.isSaving = false; // Ocultar indicador
        this.spinnerService.hide(); // Ocultar spinner
        this.navigateBack(); // O manejar el error de otra manera
      }
    );
  }

  navigateBack() {
    // Navegar a la carpeta correspondiente
    this.router.navigate(['/carpeta', this.folderName]);
  }

  onBackClicked() {
    this.onEditorRequestClose();
  }
}
>>>>>>> develop
