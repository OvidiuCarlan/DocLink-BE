ALTER TABLE Users
    ADD COLUMN user_role_id INT;

ALTER TABLE Users
    ADD CONSTRAINT FK_Users_user_roles
        FOREIGN KEY (user_role_id) REFERENCES user_roles(id);
