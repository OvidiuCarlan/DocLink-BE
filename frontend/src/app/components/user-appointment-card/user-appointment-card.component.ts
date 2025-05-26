import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppointmentData } from '../../../shared/models/appointment-model';

@Component({
  selector: 'app-user-appointment-card',
  imports: [CommonModule],
  templateUrl: './user-appointment-card.component.html',
  styleUrl: './user-appointment-card.component.scss'
})
export class UserAppointmentCardComponent {
  @Input() appointment: any;
}
