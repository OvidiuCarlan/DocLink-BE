//package com.example.appointmentservice.appointmentservice.controller;
//
//import com.example.appointmentservice.appointmentservice.business.cases.CreateAppointmentUseCase;
//import com.example.appointmentservice.appointmentservice.business.cases.GetUserAppointmentUseCase;
//import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentRequest;
//import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentResponse;
//import com.example.appointmentservice.appointmentservice.business.dto.GetUserAppointmentResponse;
//import com.example.appointmentservice.appointmentservice.domain.Appointment;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(AppointmentController.class)
//class AppointmentControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CreateAppointmentUseCase createAppointmentUseCase;
//
//    @MockBean
//    private GetUserAppointmentUseCase getUserAppointmentUseCase;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void createAppointment_WithValidRequest_ShouldReturnCreatedAppointment() throws Exception {
//        // Arrange
//        CreateAppointmentRequest request = CreateAppointmentRequest.builder()
//                .id(1L)
//                .userId(1L)
//                .postId("post-123")
//                .date("2024-12-01")
//                .time("10:00")
//                .notes("Test appointment")
//                .build();
//
//        CreateAppointmentResponse response = CreateAppointmentResponse.builder()
//                .appointmentId("appointment-123")
//                .build();
//
//        when(createAppointmentUseCase.createAppointment(any(CreateAppointmentRequest.class))).thenReturn(response);
//
//        // Act & Assert
//        mockMvc.perform(post("/appointments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.appointmentId").value("appointment-123"));
//    }
//
//    @Test
//    void getAppointments_WithValidUserId_ShouldReturnAppointments() throws Exception {
//        // Arrange
//        Appointment appointment = Appointment.builder()
//                .id("appointment-1")
//                .userId("1")
//                .postId("post-1")
//                .date("2024-12-01")
//                .time("10:00")
//                .notes("Test appointment")
//                .build();
//
//        GetUserAppointmentResponse response = GetUserAppointmentResponse.builder()
//                .appointments(Arrays.asList(appointment))
//                .build();
//
//        when(getUserAppointmentUseCase.getUserAppointments(any())).thenReturn(response);
//
//        // Act & Assert
//        mockMvc.perform(get("/appointments/1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.appointments").isArray())
//                .andExpect(jsonPath("$.appointments[0].id").value("appointment-1"))
//                .andExpect(jsonPath("$.appointments[0].userId").value("1"));
//    }
//}