package com.gym.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gym.model.User;
import com.gym.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Transactional
    public User register(User user) {
        try {
            logger.info("Attempting to register user: {}", user.getEmail());
            
            // Validate input
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }
            
            // Check if user with this email already exists
            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                logger.warn("User with email {} already exists", user.getEmail());
                throw new RuntimeException("User with this email already exists");
            }
            
            // Encode password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // Save the user
            User savedUser = userRepository.save(user);
            logger.info("User registered successfully with ID: {}", savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<User> login(String email, String password) {
        logger.info("Attempting login for user: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Login attempt with empty email");
            return Optional.empty();
        }
        
        if (password == null || password.trim().isEmpty()) {
            logger.warn("Login attempt with empty password for email: {}", email);
            return Optional.empty();
        }
        
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            logger.info("Login successful for user: {}", email);
            return user;
        }
        logger.warn("Login failed for user: {}", email);
        return Optional.empty();
    }

    @Transactional
    public User updateUser(Long id, User updatedUser) {
        logger.info("Attempting to update user with ID: {}", id);
        
        if (id == null) {
            logger.warn("Attempt to update user with null ID");
            return null;
        }
        
        return userRepository.findById(id).map(user -> {
            // Update only non-null fields
            if (updatedUser.getName() != null) {
                user.setName(updatedUser.getName());
            }
            
            if (updatedUser.getEmail() != null) {
                // Check if the new email is already used by another user
                if (!user.getEmail().equals(updatedUser.getEmail())) {
                    Optional<User> existingUser = userRepository.findByEmail(updatedUser.getEmail());
                    if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                        logger.warn("Email {} is already in use by another user", updatedUser.getEmail());
                        throw new RuntimeException("Email is already in use by another user");
                    }
                }
                user.setEmail(updatedUser.getEmail());
            }
            
            if (updatedUser.getPhone() != null) {
                user.setPhone(updatedUser.getPhone());
            }
            
            if (updatedUser.getRole() != null) {
                user.setRole(updatedUser.getRole());
            }
            // Don't update password from this method unless explicitly implemented
            
            User saved = userRepository.save(user);
            logger.info("User updated successfully with ID: {}", id);
            return saved;
        }).orElseGet(() -> {
            logger.warn("User not found for update with ID: {}", id);
            return null;
        });
    }

    @Transactional
    public boolean deleteUser(Long id) {
        logger.info("Attempting to delete user with ID: {}", id);
        
        if (id == null) {
            logger.warn("Attempt to delete user with null ID");
            return false;
        }
        
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("User deleted successfully with ID: {}", id);
            return true;
        }
        logger.warn("User not found for deletion with ID: {}", id);
        return false;
    }
    
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        logger.info("Checking if email exists: {}", email);
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void resetPassword(String email) {
        logger.info("Attempting password reset for: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with this email does not exist"));

        // Generate a random 8-character temporary password
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        
        // Encode and save
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        // Send email
        emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), tempPassword);
        logger.info("Password reset successful, email sent to: {}", email);
    }
}