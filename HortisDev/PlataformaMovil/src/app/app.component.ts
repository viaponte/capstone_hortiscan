import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { AuthService } from './services/authservice/authservice.service'; // Asegúrate de que la ruta sea correcta
import { Platform } from '@ionic/angular';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit {

  constructor(
    private titleService: Title,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService,  // Inyecta AuthService para verificar la autenticación
    private platform: Platform  // Inyecta Platform para inicializar Ionic
  ) {
    this.initializeApp(); // Inicializar la app
  }

  ngOnInit() {
    // Verificar si el usuario está autenticado y redirigir a la página correspondiente
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/folders']);  // Redirige a folders si está autenticado
    } else {
      this.router.navigate(['/login']);  // Redirige al login si no está autenticado
    }

    // Cambiar el título de la página según la navegación
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => {
        let child = this.activatedRoute.firstChild;
        while (child?.firstChild) {
          child = child.firstChild;
        }
        return child?.snapshot.data['title'] || 'HortiScan'; // Título por defecto
      })
    ).subscribe((pageTitle: string) => {
      this.titleService.setTitle(`HortiScan - ${pageTitle}`);
    });
  }

  // Método para inicializar cualquier configuración al arrancar la app en dispositivos móviles
  initializeApp() {
    this.platform.ready().then(() => {
      // Aquí puedes agregar cualquier lógica que necesites al iniciar la app móvil, como inicializar plugins de Ionic
      console.log('Plataforma lista');
    });
  }
}
