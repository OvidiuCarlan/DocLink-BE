package com.example.doclink.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WelcomeEmailService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${welcome.email.function.url}")
    private String welcomeEmailFunctionUrl;

    @Value("${welcome.email.enabled:true}")
    private boolean welcomeEmailEnabled;

    public WelcomeEmailService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public void sendWelcomeEmail(Long userId, String firstName, String lastName, String email, String role) {
        if (!welcomeEmailEnabled) {
            System.out.println("Welcome email is disabled, skipping for user: " + email);
            return;
        }

        try {
            // Create request payload
            WelcomeEmailRequest request = new WelcomeEmailRequest(userId, firstName, lastName, email, role);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create HTTP entity
            HttpEntity<WelcomeEmailRequest> entity = new HttpEntity<>(request, headers);

            // Make the request
            ResponseEntity<String> response = restTemplate.exchange(
                    welcomeEmailFunctionUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Welcome email sent successfully to: " + email);
            } else {
                System.err.println("Failed to send welcome email. Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Error sending welcome email to " + email + ": " + e.getMessage());
            // Don't fail user creation if email fails
        }
    }

    // DTO for the request
    public static class WelcomeEmailRequest {
        private Long userId;
        private String firstName;
        private String lastName;
        private String email;
        private String role;

        public WelcomeEmailRequest(Long userId, String firstName, String lastName, String email, String role) {
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.role = role;
        }

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}