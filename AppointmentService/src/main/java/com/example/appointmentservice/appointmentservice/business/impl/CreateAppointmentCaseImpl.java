package com.example.appointmentservice.appointmentservice.business.impl;

import com.example.appointmentservice.appointmentservice.business.cases.CreateAppointmentUseCase;
import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentRequest;
import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentResponse;
import com.example.appointmentservice.appointmentservice.messaging.AppointmentNotificationMessage;
import com.example.appointmentservice.appointmentservice.messaging.AppointmentNotificationPublisher;
import com.example.appointmentservice.appointmentservice.persistance.AppointmentRepository;
import com.example.appointmentservice.appointmentservice.persistance.entity.AppointmentEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateAppointmentCaseImpl implements CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentNotificationPublisher notificationPublisher;

    public CreateAppointmentResponse createAppointment(CreateAppointmentRequest request) {
        AppointmentEntity savedAppointment = savedNewAppointment(request);

        AppointmentNotificationMessage notification = AppointmentNotificationMessage.builder()
                .appointmentId(savedAppointment.getId())
                .userId(String.valueOf(request.getUserId()))
                .date(request.getDate())
                .time(request.getTime())
                .message("You have a new appointment scheduled for " + request.getDate() + " at " + request.getTime())
                .build();

        notificationPublisher.publishAppointmentCreatedNotification(notification);

        return CreateAppointmentResponse.builder()
                .appointmentId(savedAppointment.getId())
                .build();
    }

    private AppointmentEntity savedNewAppointment(CreateAppointmentRequest request){
        AppointmentEntity newAppointment = AppointmentEntity.builder()
                .userId(String.valueOf(request.getUserId()))
                .postId(request.getPostId())
                .date(request.getDate())
                .time(request.getTime())
                .notes(request.getNotes())
                .build();

        return appointmentRepository.save(newAppointment);
    }
}
