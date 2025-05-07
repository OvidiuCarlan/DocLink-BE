package com.example.appointmentservice.appointmentservice.business.impl;

import com.example.appointmentservice.appointmentservice.business.cases.GetUserAppointmentUseCase;
import com.example.appointmentservice.appointmentservice.business.dto.GetUserAppointmentResponse;
import com.example.appointmentservice.appointmentservice.business.dto.GetUserAppointmentsRequest;
import com.example.appointmentservice.appointmentservice.domain.Appointment;
import com.example.appointmentservice.appointmentservice.persistance.AppointmentRepository;
import com.example.appointmentservice.appointmentservice.persistance.entity.AppointmentEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GetUserAppointmentsUseCaseImpl implements GetUserAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    @Override
    public GetUserAppointmentResponse getUserAppointments(GetUserAppointmentsRequest request) {
        List<AppointmentEntity> results;

        if(request.getUserId() != null){
            results = appointmentRepository.findByUserId(request.getUserId());
        }
        else{
            results = new ArrayList<>();
        }
        final GetUserAppointmentResponse response = new GetUserAppointmentResponse();
        List<Appointment> appointments = results
                .stream()
                .map(AppointmentConverter::convert)
                .toList();
        response.setAppointments(appointments);

        return response;
    }
}
