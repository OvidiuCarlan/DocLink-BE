package com.example.appointmentservice.appointmentservice.controller;

import com.example.appointmentservice.appointmentservice.business.cases.CreateAppointmentUseCase;
import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentRequest;
import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@AllArgsConstructor
public class AppointmentController {
    private final CreateAppointmentUseCase createAppointmentUseCase;

    @PostMapping()
    public ResponseEntity<CreateAppointmentResponse> createAppointment(@RequestBody @Valid CreateAppointmentRequest request){

        CreateAppointmentResponse response = createAppointmentUseCase.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
