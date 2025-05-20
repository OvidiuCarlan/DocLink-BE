package com.example.doclink.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentNotificationMessage implements Serializable {
    private String appointmentId;
    private String userId;
    private String date;
    private String time;
    private String message;
}