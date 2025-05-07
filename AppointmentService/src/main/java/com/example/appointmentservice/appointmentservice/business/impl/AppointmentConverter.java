package com.example.appointmentservice.appointmentservice.business.impl;

import com.example.appointmentservice.appointmentservice.domain.Appointment;
import com.example.appointmentservice.appointmentservice.persistance.AppointmentRepository;
import com.example.appointmentservice.appointmentservice.persistance.entity.AppointmentEntity;

public class AppointmentConverter {

    public static Appointment convert(AppointmentEntity appointment){
        return Appointment.builder()
                .id(appointment.getId())
                .userId(appointment.getUserId())
                .postId(appointment.getPostId())
                .date(appointment.getDate())
                .time(appointment.getTime())
                .notes(appointment.getNotes())
                .build();
    }
}
