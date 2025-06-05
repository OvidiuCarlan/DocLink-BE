package com.example.appointmentservice.appointmentservice.persistance;

import com.example.appointmentservice.appointmentservice.persistance.entity.AppointmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AppointmentRepository extends MongoRepository<AppointmentEntity, String> {
    List<AppointmentEntity> findByUserId(String userId);

}
