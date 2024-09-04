import { Component } from '@angular/core';
import { FooterComponent } from "../footer/footer.component";


@Component({
  selector: 'app-header',
  standalone: true,
  imports: [FooterComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {

}
