package com.example.appointmentservice.appointmentservice.business.cases;

import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentRequest;
import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentResponse;

public interface CreateAppointmentUseCase {
    CreateAppointmentResponse createAppointment(CreateAppointmentRequest request);
}
