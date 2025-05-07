package com.example.appointmentservice.appointmentservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    private String id;
    private String userId;
    private String postId;
    private String date;
    private String time;
    private String notes;
}
