import { Component, ElementRef, ViewChild, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-form',
  standalone: true,
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss']
})
export class FormComponent implements AfterViewInit {

  // Referencias a los elementos del DOM
  @ViewChild('modalOpciones') modalOpciones!: ElementRef;
  @ViewChild('guardarBtn') guardarBtn!: ElementRef;
  @ViewChild('closeModal') closeModal!: ElementRef;
  @ViewChild('modalCorreo') modalCorreo!: ElementRef;
  @ViewChild('openEmailModal') openEmailModal!: ElementRef;
  @ViewChild('closeEmailModal') closeEmailModal!: ElementRef;
  @ViewChild('backToOptions') backToOptions!: ElementRef;

  ngAfterViewInit() {
    // Mostrar el modal al hacer clic en "Guardar"
    this.guardarBtn.nativeElement.onclick = () => {
      this.modalOpciones.nativeElement.style.display = 'block';
    };

    // Cerrar el modal al hacer clic en la "X"
    this.closeModal.nativeElement.onclick = () => {
      this.modalOpciones.nativeElement.style.display = 'none';
    };

    // Mostrar el segundo modal (correo electrónico) al hacer clic en "Enviar por Correo"
    this.openEmailModal.nativeElement.onclick = () => {
      this.modalOpciones.nativeElement.style.display = 'none';
      this.modalCorreo.nativeElement.style.display = 'block';
    };

    // Cerrar el segundo modal al hacer clic en la "X"
    this.closeEmailModal.nativeElement.onclick = () => {
      this.modalCorreo.nativeElement.style.display = 'none';
    };

    // Volver al primer modal al hacer clic en la flecha
    this.backToOptions.nativeElement.onclick = () => {
      this.modalCorreo.nativeElement.style.display = 'none';
      this.modalOpciones.nativeElement.style.display = 'block';
    };
  }
}