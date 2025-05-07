package com.example.appointmentservice.appointmentservice.business.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetUserAppointmentsRequest {
    @NonNull
    private String userId;
}
