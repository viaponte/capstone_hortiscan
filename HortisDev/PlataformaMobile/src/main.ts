import { bootstrapApplication } from '@angular/platform-browser';
import { RouteReuseStrategy, provideRouter, withPreloading, PreloadAllModules } from '@angular/router';
import { IonicRouteStrategy, provideIonicAngular } from '@ionic/angular/standalone';

import { routes } from './app/app.routes';  // Rutas configuradas en app.routes
import { AppComponent } from './app/app.component';  // Componente raíz de la aplicación

// Iniciar la aplicación utilizando AppComponent como raíz y con las rutas y páginas independientes
bootstrapApplication(AppComponent, {
  providers: [
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },  // Estrategia de reutilización de rutas de Ionic
    provideIonicAngular(),  // Provee los componentes y servicios de Ionic
    provideRouter(routes, withPreloading(PreloadAllModules)),  // Configura las rutas con pre-carga de módulos
  ],
});