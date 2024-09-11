import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter, map } from 'rxjs';
import { AuthInterceptor } from './services/authinterceptor/auth.interceptor';
import { AuthService } from './services/authservice/authservice.service';
import { HeaderComponent } from './shared/common/header/header.component';
import { FooterComponent } from './shared/common/footer/footer.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, FooterComponent, HttpClientModule],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  constructor(
    private titleService: Title,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService  // Inyecta AuthService para verificar la autenticación
  ) {}

  ngOnInit() {
    // Redirige al menú si el usuario ya está autenticado
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/menu']);  // Asegúrate de que esta ruta esté definida
    }

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      map(() => {
        let child = this.activatedRoute.firstChild;
        while (child?.firstChild) {
          child = child.firstChild;
        }
        return child?.snapshot.data['title'] || 'HortiScan'
      })
    ).subscribe((pageTitle: string) => {
      this.titleService.setTitle(`HortiScan - ${pageTitle}`)
    });
  }
}