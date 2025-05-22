package com.example.appointmentservice.appointmentservice.business.impl;

import com.example.appointmentservice.appointmentservice.business.dto.GetUserAppointmentResponse;
import com.example.appointmentservice.appointmentservice.business.dto.GetUserAppointmentsRequest;
import com.example.appointmentservice.appointmentservice.persistance.AppointmentRepository;
import com.example.appointmentservice.appointmentservice.persistance.entity.AppointmentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserAppointmentsUseCaseImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private GetUserAppointmentsUseCaseImpl getUserAppointmentsUseCase;

    private List<AppointmentEntity> appointmentEntities;

    @BeforeEach
    void setUp() {
        appointmentEntities = Arrays.asList(
                AppointmentEntity.builder()
                        .id("appointment-1")
                        .userId("1")
                        .postId("post-1")
                        .date("2024-12-01")
                        .time("10:00")
                        .notes("First appointment")
                        .build(),
                AppointmentEntity.builder()
                        .id("appointment-2")
                        .userId("1")
                        .postId("post-2")
                        .date("2024-12-02")
                        .time("14:00")
                        .notes("Second appointment")
                        .build()
        );
    }

    @Test
    void getUserAppointments_WithValidUserId_ShouldReturnAppointments() {
        // Arrange
        GetUserAppointmentsRequest request = GetUserAppointmentsRequest.builder()
                .userId("1")
                .build();

        when(appointmentRepository.findByUserId("1")).thenReturn(appointmentEntities);

        // Act
        GetUserAppointmentResponse response = getUserAppointmentsUseCase.getUserAppointments(request);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getAppointments().size());

        verify(appointmentRepository).findByUserId("1");
    }

    @Test
    void getUserAppointments_WithNullUserId_ShouldReturnEmptyResponse() {
        // Arrange
        GetUserAppointmentsRequest request = GetUserAppointmentsRequest.builder()
                .userId(null)
                .build();

        // Act
        GetUserAppointmentResponse response = getUserAppointmentsUseCase.getUserAppointments(request);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getAppointments().size());

        verify(appointmentRepository, never()).findByUserId(any());
    }

    @Test
    void getUserAppointments_ShouldConvertEntitiesToDomainObjects() {
        // Arrange
        GetUserAppointmentsRequest request = GetUserAppointmentsRequest.builder()
                .userId("1")
                .build();

        when(appointmentRepository.findByUserId("1")).thenReturn(appointmentEntities);

        // Act
        GetUserAppointmentResponse response = getUserAppointmentsUseCase.getUserAppointments(request);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getAppointments().size());

        assertEquals("appointment-1", response.getAppointments().get(0).getId());
        assertEquals("1", response.getAppointments().get(0).getUserId());
        assertEquals("post-1", response.getAppointments().get(0).getPostId());
        assertEquals("2024-12-01", response.getAppointments().get(0).getDate());
        assertEquals("10:00", response.getAppointments().get(0).getTime());
        assertEquals("First appointment", response.getAppointments().get(0).getNotes());
    }
}