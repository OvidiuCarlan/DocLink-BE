package com.example.appointmentservice.appointmentservice.business.impl;

import com.example.appointmentservice.appointmentservice.business.cases.CreateAppointmentUseCase;
import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentRequest;
import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentResponse;
import com.example.appointmentservice.appointmentservice.persistance.AppointmentRepository;
import com.example.appointmentservice.appointmentservice.persistance.entity.AppointmentEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateAppointmentCaseImpl implements CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    @Override
    public CreateAppointmentResponse createAppointment(CreateAppointmentRequest request) {
        AppointmentEntity savedAppointment = savedNewAppointment(request);

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
