package com.example.appointmentservice.appointmentservice.business.impl;

import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentRequest;
import com.example.appointmentservice.appointmentservice.business.dto.CreateAppointmentResponse;
import com.example.appointmentservice.appointmentservice.messaging.AppointmentNotificationMessage;
import com.example.appointmentservice.appointmentservice.messaging.AppointmentNotificationPublisher;
import com.example.appointmentservice.appointmentservice.persistance.AppointmentRepository;
import com.example.appointmentservice.appointmentservice.persistance.entity.AppointmentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAppointmentCaseImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentNotificationPublisher notificationPublisher;

    @InjectMocks
    private CreateAppointmentCaseImpl createAppointmentUseCase;

    private CreateAppointmentRequest request;
    private AppointmentEntity savedAppointment;

    @BeforeEach
    void setUp() {
        request = CreateAppointmentRequest.builder()
                .id(1L)
                .userId(1L)
                .postId("post-123")
                .date("2024-12-01")
                .time("10:00")
                .notes("Test appointment")
                .build();

        savedAppointment = AppointmentEntity.builder()
                .id("appointment-123")
                .userId("1")
                .postId("post-123")
                .date("2024-12-01")
                .time("10:00")
                .notes("Test appointment")
                .build();
    }

    @Test
    void createAppointment_WithValidRequest_ShouldReturnCreateAppointmentResponse() {
        // Arrange
        when(appointmentRepository.save(any(AppointmentEntity.class))).thenReturn(savedAppointment);

        // Act
        CreateAppointmentResponse response = createAppointmentUseCase.createAppointment(request);

        // Assert
        assertNotNull(response);
        assertEquals("appointment-123", response.getAppointmentId());

        ArgumentCaptor<AppointmentEntity> appointmentCaptor = ArgumentCaptor.forClass(AppointmentEntity.class);
        verify(appointmentRepository).save(appointmentCaptor.capture());

        AppointmentEntity capturedAppointment = appointmentCaptor.getValue();
        assertEquals("1", capturedAppointment.getUserId());
        assertEquals("post-123", capturedAppointment.getPostId());
        assertEquals("2024-12-01", capturedAppointment.getDate());
        assertEquals("10:00", capturedAppointment.getTime());
        assertEquals("Test appointment", capturedAppointment.getNotes());
    }

    @Test
    void createAppointment_ShouldPublishNotification() {
        // Arrange
        when(appointmentRepository.save(any(AppointmentEntity.class))).thenReturn(savedAppointment);

        // Act
        createAppointmentUseCase.createAppointment(request);

        // Assert
        ArgumentCaptor<AppointmentNotificationMessage> notificationCaptor =
                ArgumentCaptor.forClass(AppointmentNotificationMessage.class);
        verify(notificationPublisher).publishAppointmentCreatedNotification(notificationCaptor.capture());

        AppointmentNotificationMessage capturedNotification = notificationCaptor.getValue();
        assertEquals("appointment-123", capturedNotification.getAppointmentId());
        assertEquals("1", capturedNotification.getUserId());
        assertEquals("2024-12-01", capturedNotification.getDate());
        assertEquals("10:00", capturedNotification.getTime());
        assertEquals("You have a new appointment scheduled for 2024-12-01 at 10:00",
                capturedNotification.getMessage());
    }

    @Test
    void createAppointment_ShouldConvertUserIdToString() {
        // Arrange
        when(appointmentRepository.save(any(AppointmentEntity.class))).thenReturn(savedAppointment);

        // Act
        createAppointmentUseCase.createAppointment(request);

        // Assert
        ArgumentCaptor<AppointmentEntity> appointmentCaptor = ArgumentCaptor.forClass(AppointmentEntity.class);
        verify(appointmentRepository).save(appointmentCaptor.capture());

        AppointmentEntity capturedAppointment = appointmentCaptor.getValue();
        assertEquals("1", capturedAppointment.getUserId());
    }
}