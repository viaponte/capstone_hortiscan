import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { apiUrl } from '../../enviroment/enviroment';
import { HeaderComponent } from "../../shared/common/header/header.component";
import { OnlyofficeService } from '../../services/onlyofficeservice/onlyoffice.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-onlyoffice-editor',
  standalone: true,
  imports: [HeaderComponent],
  templateUrl: './onlyoffice-editor.component.html',
  styleUrl: './onlyoffice-editor.component.scss'
})
export class OnlyofficeEditorComponent implements OnInit {
  config: any;
  folderName!: string;
  fileName!: string;

  constructor(private http: HttpClient, private route: ActivatedRoute, private officeService: OnlyofficeService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.folderName = params['folderName'];
      this.fileName = params['fileName'];

      console.log(this.folderName, this.fileName)
      setTimeout(() => {
        if(this.folderName, this.fileName) {
          this.getDocument();
        }
      }, 0);
    });
  }

  getDocument() {
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


