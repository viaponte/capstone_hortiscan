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
  isPasswordVisible: boolean = false; // Controla la visibilidad de la contraseña
  isConfirmPasswordVisible: boolean = false; // Controla la visibilidad de confirmar contraseña

  constructor(private router: Router, private authService: AuthService) {
    // Definir el formulario reactivo con validaciones
    this.newUserForm = new FormGroup({
      username: new FormControl('', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(10)
      ]),
      password: new FormControl('', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(10)
      ]),
      confirmPassword: new FormControl('', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(10)
      ]),
    }, { validators: this.passwordsMatchValidator() });
    
  }

  ngOnInit() { }

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
        if (response && response.jwt) {
          this.authService.saveSession(response.jwt, username); // Usa saveSession para guardar tanto el token como el nombre de usuario
          this.router.navigate(['/folders']); // Asegúrate de que esta ruta sea la correcta
        } else {
          alert('No se pudo recibir el token. Login fallido.');
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

  // Método para alternar la visibilidad de la contraseña
  togglePasswordVisibility() {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  // Método para alternar la visibilidad de confirmar contraseña
  toggleConfirmPasswordVisibility() {
    this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible;
  }

  normalizeUsername(event: Event) {
    const input = event.target as HTMLInputElement;
    input.value = input.value.toLowerCase().replace(/[^a-z]/g, ''); // Solo letras minúsculas
    this.newUserForm.get('username')?.setValue(input.value, { emitEvent: false });
  }

  // Método para bloquear caracteres no permitidos en el campo de contraseña y confirmar contraseña
  validatePasswordInput(event: KeyboardEvent) {
    const input = event.target as HTMLInputElement;
    const invalidChars = /[\[\]{\}()=\s]/g; // Solo bloquea estos caracteres específicos

    // Si el carácter presionado no es válido, lo bloqueamos
    if (invalidChars.test(event.key)) {
      event.preventDefault(); // Bloquea la tecla presionada
    }
  }

  // Método para prevenir el pegado de caracteres no permitidos
  preventInvalidPaste(event: ClipboardEvent) {
    const clipboardData = event.clipboardData?.getData('text') || '';
    const invalidChars = /[\[\]{\}()=\s]/g; // Solo bloquea estos caracteres específicos

    // Si el contenido pegado contiene caracteres no permitidos, lo bloqueamos
    if (invalidChars.test(clipboardData)) {
      event.preventDefault(); // Bloquea el pegado
    }
  }

  // Método para asegurar que se valide la entrada de ambos campos (contraseña y confirmar contraseña)
  onInputChange(event: Event) {

    const input = event.target as HTMLInputElement;
    const invalidChars = /[\[\]{\}()=\s]/g; // Solo bloquea estos caracteres específicos

    // Reemplaza cualquier carácter inválido en tiempo real
    if (invalidChars.test(input.value)) {
      input.value = input.value.replace(invalidChars, ''); // Elimina caracteres no válidos
      this.newUserForm.get(input.name)?.setValue(input.value); // Sincroniza con el modelo
    }
  }


}
