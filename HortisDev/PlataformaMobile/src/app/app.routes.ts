import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'home',
    loadComponent: () => import('./home/home.page').then((m) => m.HomePage),
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.page').then( m => m.LoginPage)
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register.page').then( m => m.RegisterPage)
  },
  {
    path: 'folders',
    loadComponent: () => import('./pages/folder-views/folders/folders.page').then( m => m.FoldersPage)
  },
  {
    path: 'folder-content',
    loadComponent: () => import('./pages/folder-views/folder-content/folder-content.page').then( m => m.FolderContentPage)
  },
  {
    path: 'create-folder',
    loadComponent: () => import('./pages/folder-views/create-folder/create-folder.page').then( m => m.CreateFolderPage)
  },
];
