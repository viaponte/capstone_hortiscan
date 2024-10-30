import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../../services/authservice/authservice.service';
import { SyncService } from '../../../services/syncservice/sync.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [HttpClientModule, RouterModule, FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username: string = 'lucas'; 
  password: string = 'lucas';

  constructor(private router: Router, private authService: AuthService, private syncService: SyncService) {}

  onLogin(form: NgForm) {
    if (form.valid) {
      this.authService.login(this.username, this.password)
        .pipe(
          catchError(error => {
            console.error('Login error', error);
            return of(null);
          })
        )
        .subscribe(response => {
          if (response && response.jwt) {
            this.authService.saveSession(response.jwt, this.username);
            this.syncService.initSyncCarpetas();
            this.router.navigate(['/menu']);
          } else {
            alert('Error al iniciar sesión');
          }
        });
    }
  }  
}