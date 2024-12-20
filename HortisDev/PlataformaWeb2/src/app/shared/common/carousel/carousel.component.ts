import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-carousel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './carousel.component.html',
  styleUrl: './carousel.component.scss'
})
export class CarouselComponent implements OnInit {
  images: string[] = [
    'assets/campo_1.webp',
    'assets/campo_2.webp',
    'assets/campo_3.webp',
  ]; // Rutas de las imágenes
  currentImageIndex: number = 0;

  ngOnInit(): void {
    this.startCarousel();
  }

  startCarousel(): void {
    setInterval(() => {
      this.nextImage();
    }, 15000); // Cambia cada 5 segundos
  }

  nextImage(): void {
    this.currentImageIndex =
      (this.currentImageIndex + 1) % this.images.length; // Salta directamente a la primera al finalizar
  }
}