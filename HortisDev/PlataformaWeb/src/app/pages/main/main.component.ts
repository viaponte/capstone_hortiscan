import { Component } from '@angular/core';
import { HeaderComponent } from '../../shared/common/header/header.component';

@Component({
  selector: 'app-main',
  standalone: true,
  imports: [HeaderComponent],
  templateUrl: './main.component.html',
  styleUrl: './main.component.scss'
})
export class MainComponent {
  // constructor(private login: )
}
