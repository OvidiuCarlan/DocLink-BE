package com.example.appointmentservice.appointmentservice.business.dto;

import com.example.appointmentservice.appointmentservice.domain.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserAppointmentResponse {
    private List<Appointment> appointments;
}
