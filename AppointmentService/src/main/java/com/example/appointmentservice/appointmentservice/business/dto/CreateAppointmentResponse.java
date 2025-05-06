package com.example.appointmentservice.appointmentservice.business.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentResponse {
    private String appointmentId;
}
