import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/folders',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadChildren: () => import('./login/login.module').then( m => m.LoginPageModule)
  },
  {
    path: 'home',
    loadChildren: () => import('./home/home.module').then( m => m.HomePageModule)
  },
  {
    path: 'folders',
    loadChildren: () => import('./folders/folders.module').then( m => m.FoldersPageModule)
  },
  {
    path: 'folder-content/:folderName',
    loadChildren: () => import('./folder-content/folder-content.module').then( m => m.FolderContentPageModule)
  },
  {
    path: 'create-folder',
    loadChildren: () => import('./create-folder/create-folder.module').then( m => m.CreateFolderPageModule)
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
