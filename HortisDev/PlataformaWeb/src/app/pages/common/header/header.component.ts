import { Component } from '@angular/core';
<<<<<<< HEAD
import { LoginComponent } from '../../login/login.component';
import { AuthService } from '../../../services/authservice/authservice.service';
import { Router } from '@angular/router';
=======
import { FooterComponent } from "../footer/footer.component";

>>>>>>> f1a075f18f101a4abf893d2f7ead0f7b0b180d72

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
