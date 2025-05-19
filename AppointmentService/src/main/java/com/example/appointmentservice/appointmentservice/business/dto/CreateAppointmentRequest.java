package com.example.appointmentservice.appointmentservice.business.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    private Long id;
    @NonNull
    private Long userId;
    @NonNull
    private String postId;
    @NonNull
    private String date;
    @NonNull
    private String time;
    @NonNull
    private String notes;
}
