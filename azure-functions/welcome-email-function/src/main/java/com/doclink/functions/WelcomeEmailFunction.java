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
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.FUNCTION
            ) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        Logger logger = context.getLogger();
        logger.info("Welcome email function triggered via HTTP");

        try {
            // Get the request body
            String requestBody = request.getBody().orElse("");

            if (requestBody.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
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
                        .body("Welcome email sent successfully")
                        .build();
            } else {
                logger.severe("Failed to send welcome email to: " + userEvent.getEmail());
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to send welcome email")
                        .build();
            }

        } catch (Exception e) {
            logger.severe("Error processing welcome email: " + e.getMessage());
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
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
                "<li><strong>Set up your practice</strong> - Configure your availability and appointment settings</li>" :
                "<li><strong>Find doctors</strong> - Browse and connect with healthcare providers</li>";

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <title>Welcome to DocLink</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; }
                    .header { background-color: #2c5aa0; color: white; padding: 30px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { padding: 30px; background-color: #f9f9f9; }
                    .footer { padding: 20px; text-align: center; color: #666; font-size: 14px; background-color: #e9e9e9; border-radius: 0 0 8px 8px; }
                    .button { background-color: #2c5aa0; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 20px 0; }
                    h1 { margin: 0; font-size: 28px; }
                    h2 { color: #2c5aa0; margin-bottom: 10px; }
                    h3 { color: #2c5aa0; margin-top: 25px; margin-bottom: 15px; }
                    ul { padding-left: 20px; }
                    li { margin-bottom: 8px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Welcome to DocLink!</h1>
                </div>
                <div class="content">
                    <h2>Hello %s %s,</h2>
                    <p>Welcome to DocLink! We're excited to have you join our healthcare platform as a <strong>%s</strong>.</p>
                    
                    <p>DocLink is designed to streamline healthcare communication and make managing your medical needs easier than ever. Our platform connects patients and healthcare providers in a secure, efficient environment.</p>
                    
                    <h3>What's next?</h3>
                    <ul>
                        <li><strong>Complete your profile</strong> - Add your medical information and preferences</li>
                        %s
                        <li><strong>Explore the platform</strong> - Familiarize yourself with all available features</li>
                        <li><strong>Get support</strong> - Our team is here to help you get started</li>
                    </ul>
                    
                    <p>Your account details:</p>
                    <ul>
                        <li>Email: %s</li>
                        <li>Account Type: %s</li>
                        <li>Registration Date: Today</li>
                    </ul>
                    
                    <p>If you have any questions or need assistance, our support team is here to help. Simply reply to this email or contact us through the platform.</p>
                    
                    <p>Thank you for choosing DocLink! We look forward to supporting your healthcare journey.</p>
                    
                    <p><strong>Best regards,</strong><br>The DocLink Team</p>
                </div>
                <div class="footer">
                    <p>This email was sent from DocLink. If you have any questions, please contact our support team.</p>
                    <p>&copy; 2024 DocLink. All rights reserved.</p>
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