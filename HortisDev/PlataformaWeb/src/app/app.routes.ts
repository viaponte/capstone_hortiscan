import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { ErrorComponent } from './pages/error/error.component';
import { MainComponent } from './pages/main/main.component';
import { FolderComponent } from './pages/folder/folder.component';
import { FormComponent } from './pages/form/form.component';

export const routes: Routes = [
  { path: '', redirectTo: '/formulario', pathMatch: 'full' },
  { path: 'login', component: LoginComponent, data: { title: 'Login' } },
  { path: 'menu', component: MainComponent, data: { title: 'Menu' } },
  { path: 'carpeta', component: FolderComponent, data: { title: 'Carpeta' } },
  { path: 'formulario', component: FormComponent, data: { title: 'Formulario' } },
  { path: '**', component: ErrorComponent, data: { title: 'Error' }}
];
