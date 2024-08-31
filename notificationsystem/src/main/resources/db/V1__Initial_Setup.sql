-- Create the 'users' table
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create the 'roles' table
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL
);

-- Create the join table for many-to-many relationship between users and roles
CREATE TABLE user_roles (
                            user_id INT NOT NULL,
                            role_id INT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Create the 'notifications' table
CREATE TABLE notifications (
                               id SERIAL PRIMARY KEY,
                               user_id INT NOT NULL,
                               message TEXT NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert default roles
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

-- Insert some example users (passwords should be hashed in the application)
INSERT INTO users (username, password, enabled) VALUES
                                                    ('user1', '$2a$10$hashedpassword1', TRUE),
                                                    ('user2', '$2a$10$hashedpassword2', TRUE),
                                                    ('admin', '$2a$10$hashedpassword3', TRUE);

-- Assign roles to the users
INSERT INTO user_roles (user_id, role_id) VALUES
                                              ((SELECT id FROM users WHERE username = 'user1'), (SELECT id FROM roles WHERE name = 'ROLE_USER')),
                                              ((SELECT id FROM users WHERE username = 'user2'), (SELECT id FROM roles WHERE name = 'ROLE_USER')),
                                              ((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'));

-- Insert some example notifications for the users
-- Insert test records into the notifications table with explicit created_at values
INSERT INTO notifications (user_id, message, created_at)
VALUES
    (1, 'You have a new message from support44.', CURRENT_TIMESTAMP);


INSERT INTO notifications (user_id, message, created_at)
VALUES
    (2, 'You have a new message from support4.', CURRENT_TIMESTAMP);

INSERT INTO notifications (user_id, message, created_at)
VALUES
    (3, 'You have a new message from support33.', CURRENT_TIMESTAMP);
