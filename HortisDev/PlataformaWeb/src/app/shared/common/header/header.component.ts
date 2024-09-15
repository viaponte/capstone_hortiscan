import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/authservice/authservice.service';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [FooterComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  constructor(private loginService: AuthService, private router: Router) {}

  logout() {
    this.loginService.logout();
    this.router.navigate(['/login'])
  }
}
