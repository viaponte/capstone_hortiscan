import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter, map } from 'rxjs';
import { AuthService } from './services/authservice/authservice.service';
import { HeaderComponent } from './shared/common/header/header.component';
import { FooterComponent } from './shared/common/footer/footer.component';
import { SyncService } from './services/syncservice/sync.service';
<<<<<<< HEAD
=======
import { SpinnerComponent } from "./shared/common/spinner/spinner.component";
>>>>>>> develop

@Component({
  selector: 'app-root',
  standalone: true,
<<<<<<< HEAD
  imports: [RouterOutlet, HeaderComponent, FooterComponent, HttpClientModule],
=======
  imports: [RouterOutlet, HeaderComponent, FooterComponent, HttpClientModule, SpinnerComponent],
>>>>>>> develop
  providers: [
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  constructor(
    private titleService: Title,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService,
    private syncService: SyncService
  ) {}

  ngOnInit() {
    this.isLogin();

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
  
  isLogin() {
    try {
      this.syncService.initSyncCarpetas();
    } catch (error) {
      console.error('Error desde app.component.ts: ', error);
    }
    // Redirige al menú si el usuario ya está autenticado
<<<<<<< HEAD
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/menu']);  // Asegúrate de que esta ruta esté definida
    } else {
=======
    if (!this.authService.isLoggedIn()) {
>>>>>>> develop
      this.router.navigate(['/login'])
    }
  }

}