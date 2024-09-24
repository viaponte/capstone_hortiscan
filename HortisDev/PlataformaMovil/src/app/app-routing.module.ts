import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'register',
    loadChildren: () => import('./auth/register/register.module').then( m => m.RegisterPageModule)
  },
  {
    path: 'login',
    loadChildren: () => import('./auth/login/login.module').then( m => m.LoginPageModule)
  },
  {
    path: 'folders',
    loadChildren: () => import('./folder-views/folders/folders.module').then( m => m.FoldersPageModule)
  },
  {
    path: 'create-folder',
    loadChildren: () => import('./folder-views/create-folder/create-folder.module').then( m => m.CreateFolderPageModule)
  },
  {
    path: 'folder-content/:nombreCarpeta',
    loadChildren: () => import('./folder-views/folder-content/folder-content.module').then( m => m.FolderContentPageModule)
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
