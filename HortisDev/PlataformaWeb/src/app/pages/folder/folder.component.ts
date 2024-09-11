import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { HeaderComponent } from "../../shared/common/header/header.component";



@Component({
  selector: 'app-folder',
  standalone: true,
  imports: [RouterModule, HeaderComponent],
  templateUrl: './folder.component.html',
  styleUrl: './folder.component.scss'
})

export class FolderComponent implements OnInit{
  folderId: number = 0;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.folderId = +this.route.snapshot.paramMap.get('id')!;
    console.log('Folder ID:', this.folderId);
  }
}
