import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { FolderContentPageRoutingModule } from './folder-content-routing.module';

import { FolderContentPage } from './folder-content.page';
import { HeaderComponent } from 'src/app/shared/header/header.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    FolderContentPageRoutingModule,
    HeaderComponent
  ],
  declarations: [FolderContentPage]
})
export class FolderContentPageModule {}
