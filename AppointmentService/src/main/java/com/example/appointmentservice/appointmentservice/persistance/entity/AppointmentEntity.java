package com.example.appointmentservice.appointmentservice.persistance.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "appointmentEntity")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AppointmentEntity {
    @Id
    private String id;

    private String userId;
    private String postId;
    private String date;
    private String time;
    private String notes;
}
