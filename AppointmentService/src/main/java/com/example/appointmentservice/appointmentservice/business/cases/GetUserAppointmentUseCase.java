package com.example.appointmentservice.appointmentservice.business.cases;

import com.example.appointmentservice.appointmentservice.business.dto.GetUserAppointmentResponse;
import com.example.appointmentservice.appointmentservice.business.dto.GetUserAppointmentsRequest;

public interface GetUserAppointmentUseCase {
    GetUserAppointmentResponse getUserAppointments(GetUserAppointmentsRequest request );
}
