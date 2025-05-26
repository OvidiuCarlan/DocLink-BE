import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TokenManagerService } from '../../services/token-manager.service';
import { AppointmentData } from '../../../shared/models/appointment-model';
import { AppointmentService } from '../../services/appointment.service';

@Component({
  selector: 'app-post-card',
  imports: [CommonModule, FormsModule],
  templateUrl: './post-card.component.html',
  styleUrl: './post-card.component.scss'
})
export class PostCardComponent {
  showModal = false;
  appointmentDate: string = '';
  appointmentTime: string = '';
  appointmentNotes: string = '';
  userId: any;

  constructor(private tokenManager: TokenManagerService, private appointmentService: AppointmentService){
    
  }

  @Input() post!: {
    title: string;
    content: string;
    category: string;
  };

  createAppointment() {
    this.showModal = true;
  }
  closeModal() {
    this.showModal = false;
  }
  submitAppointment() {

    this.userId = this.tokenManager.getClaims().userId;

    const newAppointment: AppointmentData = {
      userId: this.userId,
      date: this.appointmentDate,
      time: this.appointmentTime,
      notes: this.appointmentNotes
    }

    this.appointmentService.createAppointment(newAppointment).subscribe({
      next: (response) => {
        console.log('Appointment created successfully:', response);
        this.closeModal();
      },
      error: (err) => {
        console.error('Error creating appointment:', err);
      }
    });
    this.closeModal();
  }
}
