package com.example.appointmentservice.appointmentservice.controller;

import com.example.appointmentservice.appointmentservice.business.cases.CreateAppointmentUseCase;
import com.example.appointmentservice.appointmentservice.business.cases.GetUserAppointmentUseCase;
import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentRequest;
import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentResponse;
import com.example.appointmentservice.appointmentservice.business.dto.GetUserAppointmentResponse;
import com.example.appointmentservice.appointmentservice.business.dto.GetUserAppointmentsRequest;
import com.example.appointmentservice.appointmentservice.business.impl.GetUserAppointmentsUseCaseImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/appointments")
@AllArgsConstructor
public class AppointmentController {
    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final GetUserAppointmentUseCase getUserAppointmentUseCase;
    //private final GetDoctorAppointmentUseCase getDoctorAppointmentUseCase;

    @PostMapping()
    public ResponseEntity<CreateAppointmentResponse> createAppointment(@RequestBody @Valid CreateAppointmentRequest request){

        CreateAppointmentResponse response = createAppointmentUseCase.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path = "{userId}")
    private ResponseEntity<GetUserAppointmentResponse> getAppointments(
            @PathVariable(value = "userId") final String userId){

        System.out.println("Requested userId: " + userId);

        GetUserAppointmentsRequest request = GetUserAppointmentsRequest.builder()
                .userId(userId)
                .build();

        GetUserAppointmentResponse response = getUserAppointmentUseCase.getUserAppointments(request);
        System.out.println("Found appointments: " + response);
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/doctor/{doctorId}")
//    public ResponseEntity<GetDoctorAppointmentResponse> getAppointmentsByDoctor(
//            @PathVariable(value = "doctorId") final String doctorId) {
//
//        GetDoctorAppointmentsRequest request = GetDoctorAppointmentsRequest.builder()
//                .doctorId(doctorId)
//                .build();
//
//        GetDoctorAppointmentResponse response = getDoctorAppointmentUseCase.getAppointmentsByDoctor(request);
//        return ResponseEntity.ok(response);
//    }
}
