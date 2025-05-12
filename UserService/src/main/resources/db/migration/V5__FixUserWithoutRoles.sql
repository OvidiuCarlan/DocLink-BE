INSERT IGNORE INTO user_roles (role_name) VALUES ('USER'), ('DOC'), ('ADMIN');

UPDATE Users u
SET u.user_role_id = (SELECT id FROM user_roles WHERE role_name = 'USER')
WHERE u.user_role_id IS NULL;