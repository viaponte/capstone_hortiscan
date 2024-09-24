import { Component } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { AuthService } from '../../services/authservice/authservice.service';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular'; 
import { filter } from 'rxjs/operators';
import { NavController } from '@ionic/angular';
@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, IonicModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  isFoldersPage: boolean = true; // Nueva propiedad
  constructor(
    private loginService: AuthService,
    private router: Router,
    private navController: NavController
  ) {
    this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.isFoldersPage = event.url === '/folders'; // Verifica si está en la página de folders
      });
  }
  logout() {
    this.loginService.logout();
    this.router.navigate(['/login']);
  }
}