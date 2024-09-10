import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/authservice/authservice.service';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-register',
  templateUrl: './register.page.html',
  styleUrls: ['./register.page.scss'],
})
export class RegisterPage implements OnInit {
  newUserForm: FormGroup;

  constructor(private router: Router, private authService: AuthService) {
    // Definir el formulario reactivo con validaciones
    this.newUserForm = new FormGroup({
      username: new FormControl('', Validators.required),
      // email: new FormControl('', Validators.required),
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      confirmPassword: new FormControl('', Validators.required),
    }, { validators: this.passwordsMatchValidator() });
  }

  ngOnInit() {}

  // Registro de usuario
  createUser() {
    if (this.newUserForm.valid) {
      const username = this.newUserForm.get('username')?.value ?? '';
      const password = this.newUserForm.get('password')?.value ?? '';

      this.authService.signup(username, password)
        .pipe(
          catchError(error => {
            console.log('signup error', error);
            alert('Error al registrarse');
            return of(null);
          })
        )
        .subscribe(response => {
          if (response) {
            this.login(username, password);
          }
        });
    }
  }

  // Iniciar sesión después del registro
  login(username: string, password: string) {
    this.authService.login(username, password)
      .pipe(
        catchError(error => {
          console.error('Login error', error);
          alert('Logeo fallido. Checkea tus credenciales.');
          return of(null);
        })
      )
      .subscribe(response => {
        if (response && response.jtw) {
          this.authService.saveToken(response.jtw);
          this.router.navigate(['/folders']);
        }
      });
  }

  // Limpiar los campos del formulario
  clearInputs() {
    this.newUserForm.reset();
  }

  // Validador personalizado para confirmar que las contraseñas coinciden
  passwordsMatchValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const password = control.get('password')?.value;
      const confirmPassword = control.get('confirmPassword')?.value;
  
      if (password && confirmPassword && password !== confirmPassword) {
        return { passwordsMismatch: true };
      }
      return null;
    };
  }
}
