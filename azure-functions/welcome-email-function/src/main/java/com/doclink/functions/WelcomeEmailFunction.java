package com.doclink.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import java.util.Optional;
import java.util.logging.Logger;

public class WelcomeEmailFunction {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FunctionName("SendWelcomeEmail")
    public HttpResponseMessage sendWelcomeEmail(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.POST, HttpMethod.OPTIONS},
                    authLevel = AuthorizationLevel.FUNCTION
            ) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        Logger logger = context.getLogger();

        // Handle CORS preflight requests
        if (request.getHttpMethod() == HttpMethod.OPTIONS) {
            return request.createResponseBuilder(HttpStatus.OK)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, x-functions-key")
                    .header("Access-Control-Max-Age", "3600")
                    .build();
        }

        logger.info("Welcome email function triggered via HTTP");

        try {
            // Get the request body
            String requestBody = request.getBody().orElse("");

            if (requestBody.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .header("Access-Control-Allow-Origin", "*")
                        .body("Request body is required")
                        .build();
            }

            logger.info("Processing welcome email request: " + requestBody);

            // Parse the incoming user data
            UserCreatedEvent userEvent = objectMapper.readValue(requestBody, UserCreatedEvent.class);

            logger.info("Processing welcome email for user: " + userEvent.getEmail());

            // Send welcome email
            boolean emailSent = sendWelcomeEmailInternal(userEvent, logger);

            if (emailSent) {
                logger.info("Welcome email sent successfully to: " + userEvent.getEmail());
                return request.createResponseBuilder(HttpStatus.OK)
                        .header("Access-Control-Allow-Origin", "*")
                        .body("Welcome email sent successfully")
                        .build();
            } else {
                logger.severe("Failed to send welcome email to: " + userEvent.getEmail());
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                        .header("Access-Control-Allow-Origin", "*")
                        .body("Failed to send welcome email")
                        .build();
            }

        } catch (Exception e) {
            logger.severe("Error processing welcome email: " + e.getMessage());
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Access-Control-Allow-Origin", "*")
                    .body("Error processing request: " + e.getMessage())
                    .build();
        }
    }

    private boolean sendWelcomeEmailInternal(UserCreatedEvent userEvent, Logger logger) {
        try {
            // Get configuration from environment variables
            String apiKey = System.getenv("SENDGRID_API_KEY");
            String fromEmail = System.getenv("FROM_EMAIL");

            if (apiKey == null || fromEmail == null) {
                logger.severe("Missing SendGrid configuration. Check SENDGRID_API_KEY and FROM_EMAIL settings.");
                return false;
            }

            SendGrid sendGrid = new SendGrid(apiKey);

            // Create email
            Email from = new Email(fromEmail, "DocLink Team");
            Email to = new Email(userEvent.getEmail(), userEvent.getFirstName() + " " + userEvent.getLastName());
            String subject = "Welcome to DocLink!";

            String htmlContent = buildWelcomeEmailContent(userEvent);
            Content content = new Content("text/html", htmlContent);

            Mail mail = new Mail(from, subject, to, content);

            // Send email
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            logger.info("SendGrid response status: " + response.getStatusCode());

            // SendGrid returns 202 for successful requests
            return response.getStatusCode() == 202;

        } catch (Exception e) {
            logger.severe("Error sending welcome email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String buildWelcomeEmailContent(UserCreatedEvent userEvent) {
        String roleDisplay = "DOC".equals(userEvent.getRole()) ? "Doctor" : "Patient";
        String roleSpecificContent = "DOC".equals(userEvent.getRole()) ?
                "<p>As a doctor on DocLink, you can:</p>" +
                        "<ul>" +
                        "<li>Create and manage your medical posts</li>" +
                        "<li>Connect with patients seeking your expertise</li>" +
                        "<li>Schedule and manage appointments</li>" +
                        "<li>Build your professional presence</li>" +
                        "</ul>" :
                "<p>As a patient on DocLink, you can:</p>" +
                        "<ul>" +
                        "<li>Browse medical posts from qualified doctors</li>" +
                        "<li>Book appointments with healthcare providers</li>" +
                        "<li>Access your appointment history</li>" +
                        "<li>Connect with the right healthcare professionals</li>" +
                        "</ul>";

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .footer { text-align: center; padding: 10px; color: #666; font-size: 12px; }
                    ul { margin: 10px 0; padding-left: 20px; }
                    .button { display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; margin-top: 15px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to DocLink!</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s %s,</h2>
                        
                        <p>Thank you for joining DocLink as a <strong>%s</strong>! We're excited to have you as part of our healthcare community.</p>
                        
                        %s
                        
                        <p><strong>Your account details:</strong></p>
                        <ul>
                            <li>Email: %s</li>
                            <li>Role: %s</li>
                        </ul>
                        
                        <p>You can now log in to your account and start exploring all the features DocLink has to offer.</p>
                        
                        <h3>Getting Started</h3>
                        <p>Here are some tips to help you get the most out of DocLink:</p>
                        <ol>
                            <li>Complete your profile to help others learn more about you</li>
                            <li>Explore the platform and familiarize yourself with the features</li>
                            <li>Reach out if you have any questions or need assistance</li>
                        </ol>
                        
                        <h3>Need Help?</h3>
                        <p>Our support team is here to assist you. If you have any questions or encounter any issues, please don't hesitate to contact us.</p>
                        <p>Simply reply to this email or contact us through the platform.</p>
                        
                        <p>Thank you for choosing DocLink! We look forward to supporting your healthcare journey.</p>
                        
                        <p><strong>Best regards,</strong><br>The DocLink Team</p>
                    </div>
                    <div class="footer">
                        <p>This email was sent from DocLink. If you have any questions, please contact our support team.</p>
                        <p>&copy; 2024 DocLink. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                userEvent.getFirstName(),
                userEvent.getLastName(),
                roleDisplay,
                roleSpecificContent,
                userEvent.getEmail(),
                roleDisplay
        );
    }

    // Data class for incoming user events
    public static class UserCreatedEvent {
        private Long userId;
        private String firstName;
        private String lastName;
        private String email;
        private String role;

        // Default constructor
        public UserCreatedEvent() {}

        // Constructor
        public UserCreatedEvent(Long userId, String firstName, String lastName, String email, String role) {
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