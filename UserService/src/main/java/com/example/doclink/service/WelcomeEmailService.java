package com.example.doclink.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

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
            // Log the URL being used
            System.out.println("Sending welcome email to URL: " + welcomeEmailFunctionUrl);

            // Create request payload
            WelcomeEmailRequest request = new WelcomeEmailRequest(userId, firstName, lastName, email, role);

            // Log the request payload
            String requestJson = objectMapper.writeValueAsString(request);
            System.out.println("Request payload: " + requestJson);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

            // Create HTTP entity
            HttpEntity<WelcomeEmailRequest> entity = new HttpEntity<>(request, headers);

            // Make the request
            ResponseEntity<String> response = restTemplate.exchange(
                    welcomeEmailFunctionUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.ACCEPTED) {
                System.out.println("Welcome email sent successfully to: " + email);
                System.out.println("Response: " + response.getBody());
            } else {
                System.err.println("Failed to send welcome email. Status: " + response.getStatusCode());
                System.err.println("Response body: " + response.getBody());
            }

        } catch (HttpClientErrorException e) {
            System.err.println("Client error sending welcome email to " + email + ": " + e.getMessage());
            System.err.println("Response body: " + e.getResponseBodyAsString());
            System.err.println("Status code: " + e.getStatusCode());
        } catch (HttpServerErrorException e) {
            System.err.println("Server error sending welcome email to " + email + ": " + e.getMessage());
            System.err.println("Response body: " + e.getResponseBodyAsString());
            System.err.println("Status code: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            System.err.println("Network error sending welcome email to " + email + ": " + e.getMessage());
            System.err.println("Could not connect to Azure Function. Check if the URL is correct and accessible.");
        } catch (Exception e) {
            System.err.println("Unexpected error sending welcome email to " + email + ": " + e.getMessage());
            e.printStackTrace();
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