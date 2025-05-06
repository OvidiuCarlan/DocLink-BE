package com.example.appointmentservice.appointmentservice.business.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    @NonNull
    private Long id;
    @NonNull
    private Long userId;
    @NonNull
    private Long postId;
    @NonNull
    private String date;
    @NonNull
    private String time;
    @NonNull
    private String notes;
}
