CREATE TABLE notifications (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT NOT NULL,
                               appointment_id VARCHAR(255) NOT NULL,
                               message VARCHAR(255) NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               is_read BOOLEAN DEFAULT FALSE,
                               CONSTRAINT FK_notifications_users FOREIGN KEY (user_id) REFERENCES Users(id)
);