import { Component } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
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

  constructor(private loginService: AuthService, private router: Router, private location: Location) {
    // Escuchar los cambios de ruta y actualizar showBackButton
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.showBackButton = event.url !== '/menu';
      });
  }
  logout() {
    this.loginService.logout();
    this.router.navigate(['/login'])
  }
  goBack(): void {
    window.history.back();
  }
  
  
}
