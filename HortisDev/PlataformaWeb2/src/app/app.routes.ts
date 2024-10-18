import { Routes } from '@angular/router';
import { LoginComponent } from './pages/auth/login/login.component';
import { ErrorComponent } from './pages/error/error.component';
import { FolderComponent } from './pages/folder/folder.component';
import { FormComponent } from './pages/form/form.component';
import { MainComponent } from './pages/main/main.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { OnlyofficeEditorComponent } from './pages/onlyoffice-editor/onlyoffice-editor.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent, data: { title: 'Login' } },
  { path: 'menu', component: MainComponent, data: { title: 'Menu' } },
  { path: 'carpeta/:nombreCarpeta', component: FolderComponent, data: { title: 'Carpeta' } },
  { path: 'formulario', component: FormComponent, data: { title: 'Formulario' } },
  { path: 'registrarse', component: RegisterComponent, data: { title: "Registrarse" } },
  { path: 'editor', component: OnlyofficeEditorComponent, data: { title: 'Editor' } },
  { path: '**', component: ErrorComponent, data: { title: 'Error' }},
];
