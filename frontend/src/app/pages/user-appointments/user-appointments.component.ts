import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { AppointmentData, GetAppointmentsResponse } from '../../../shared/models/appointment-model';
import { AppointmentService } from '../../services/appointment.service';
import { TokenManagerService } from '../../services/token-manager.service';
import { UserAppointmentCardComponent } from '../../components/user-appointment-card/user-appointment-card.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-user-appointments',
  imports: [NavbarComponent, UserAppointmentCardComponent, CommonModule],
  templateUrl: './user-appointments.component.html',
  styleUrl: './user-appointments.component.scss'
})
export class UserAppointmentsComponent implements OnInit{
  appointments: any[] = [];

  constructor(private appointmentService: AppointmentService, private tokenManager: TokenManagerService) {}

  ngOnInit(): void {
    const userId = this.tokenManager.getClaims().userId;

    if (userId){
      this.appointmentService.getUserAppointments(userId).subscribe((response: GetAppointmentsResponse) => {
        this.appointments = response.appointments;
      });
    }
  }
}
