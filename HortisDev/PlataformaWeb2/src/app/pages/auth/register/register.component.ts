import { CommonModule } from '@angular/common';
import { Component, ElementRef, ViewChild } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/authservice/authservice.service';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule, ReactiveFormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  newUserForm = new FormGroup({
    username: new FormControl('newuser2', Validators.required),
    // email: new FormControl('', Validators.required),
    password: new FormControl('newuser2', [Validators.required, Validators.minLength(6)]),
    confirmPassword: new FormControl('newuser2', Validators.required),
  }, { validators: this.passwordsMatchValidator() });

  constructor(private router: Router, private authService: AuthService) {}

  // Variable que captura error
  userExist: boolean = false;

  // Se registra el usuario
  createUser() {
    if(this.newUserForm.valid) {
      
      const username = this.newUserForm.get('username')?.value ?? ''; // Se obtiene el username
      const password = this.newUserForm.get('password')?.value ?? ''; // Se obtiene el password
  
      console.log(username, password);

      this.authService.signup(username, password)
        .pipe(
          catchError(error => {
            if(error.status == 409) {
              this.userExist = true;
            }
            return of(null);
          })
        )
        .subscribe(response => {
          if(response) {
            alert('Usuario registrado con exito');
            this.router.navigate(['/login']);
            // this.login(username, password); 
          }
        })
    };
  }

  // Login despues de registrarse
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
          this.authService.saveSession(response.jwt, username);
          this.router.navigate(['/menu']);
        }
      });
  }

  // Se limpian los inputs
  clearInputs() {
    // Limpiar FormGroup
    this.newUserForm.reset();
  }

  // Validador personalizado para confirmar si las contraseñas coinciden
  passwordsMatchValidator(): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const password = formGroup.get('password')?.value;
      const confirmPassword = formGroup.get('confirmPassword')?.value;
      if (password && confirmPassword && password !== confirmPassword) {
        return { passwordsMismatch: true };
      }
      return null;
    };
  }
}
