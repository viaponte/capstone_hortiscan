import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { FoldersPageRoutingModule } from './folders-routing.module';

import { FoldersPage } from './folders.page';
import { HeaderComponent } from "../../shared/header/header.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    FoldersPageRoutingModule,
    HeaderComponent
],
  declarations: [FoldersPage]
})
export class FoldersPageModule {}
