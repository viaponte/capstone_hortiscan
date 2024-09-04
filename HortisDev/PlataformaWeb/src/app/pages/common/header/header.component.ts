import { Component } from '@angular/core';
import { LoginComponent } from '../../login/login.component';
import { AuthService } from '../../../services/authservice/authservice.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [],
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
