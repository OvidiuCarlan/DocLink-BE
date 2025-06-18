package com.example.doclink.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

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
        this.restTemplate.getMessageConverters().add(0, new org.springframework.http.converter.StringHttpMessageConverter(java.nio.charset.StandardCharsets.UTF_8));
        this.objectMapper = new ObjectMapper();

        // Set timeout for external API calls
        this.restTemplate.setRequestFactory(new org.springframework.http.client.SimpleClientHttpRequestFactory());
        ((org.springframework.http.client.SimpleClientHttpRequestFactory) this.restTemplate.getRequestFactory()).setConnectTimeout(Duration.ofSeconds(10));
        ((org.springframework.http.client.SimpleClientHttpRequestFactory) this.restTemplate.getRequestFactory()).setReadTimeout(Duration.ofSeconds(30));
    }

    public void sendWelcomeEmail(Long userId, String firstName, String lastName, String email, String role) {
        if (!welcomeEmailEnabled) {
            System.out.println("Welcome email is disabled, skipping for user: " + email);
            return;
        }

        System.out.println("Attempting to send welcome email to: " + email);
        System.out.println("Using function URL: " + welcomeEmailFunctionUrl);

        try {
            // Validate URL
            if (welcomeEmailFunctionUrl == null || welcomeEmailFunctionUrl.trim().isEmpty()) {
                System.err.println("Welcome email function URL is not configured");
                return;
            }

            // Create request payload
            WelcomeEmailRequest request = new WelcomeEmailRequest(userId, firstName, lastName, email, role);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("User-Agent", "DocLink-UserService/1.0");

            // Create HTTP entity
            HttpEntity<WelcomeEmailRequest> entity = new HttpEntity<>(request, headers);

            System.out.println("Sending request to Azure Function...");
            System.out.println("Request payload: " + objectMapper.writeValueAsString(request));

            // Make the request
            ResponseEntity<String> response = restTemplate.exchange(
                    welcomeEmailFunctionUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Welcome email sent successfully to: " + email);
                System.out.println("Response: " + response.getBody());
            } else {
                System.err.println("Failed to send welcome email. Status: " + response.getStatusCode());
                System.err.println("Response body: " + response.getBody());
            }

        } catch (RestClientException e) {
            System.err.println("Network error sending welcome email to " + email + ": " + e.getMessage());
            System.err.println("Exception type: " + e.getClass().getSimpleName());
            e.printStackTrace();
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

        public WelcomeEmailRequest() {}

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