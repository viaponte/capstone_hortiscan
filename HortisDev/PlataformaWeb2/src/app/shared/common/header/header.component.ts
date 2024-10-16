import { Component } from '@angular/core';
import { NavigationEnd, Router, Event } from '@angular/router';
import { AuthService } from '../../../services/authservice/authservice.service';
import { FooterComponent } from '../footer/footer.component';
import { Location, CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [FooterComponent, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  showBackButton: boolean = true;
  isInMenu: boolean = false; // Variable para controlar si estás en el menú

  constructor(private loginService: AuthService, private router: Router, private location: Location) {
    // Escuchar los cambios de ruta y actualizar showBackButton e isInMenu
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: Event) => {
        if (event instanceof NavigationEnd) {
          this.showBackButton = event.url !== '/menu';
          this.isInMenu = event.url === '/menu'; // Actualizar el estado de isInMenu
        }
      });
  }

  logout() {
    this.loginService.logout();
    this.router.navigate(['/login']);
  }
  
  goBack(): void {
    if (!this.isInMenu) { // Solo va atrás si no estás en el menú
      window.history.back();
    }
  }

  goEditor() {
    this.router.navigate(['/editor']);
  }
}
