import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SpinnerService } from '../../services/spinnerservice/spinner.service';

@Component({
  selector: 'app-spinner',
  templateUrl: './spinner.component.html',
  styleUrls: ['./spinner.component.scss'],
})

export class SpinnerComponent{
  
  constructor(public spinnerService: SpinnerService) {}
  
}