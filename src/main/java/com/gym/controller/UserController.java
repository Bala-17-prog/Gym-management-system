package com.gym.controller;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.model.User;
import com.gym.service.UserService;
import com.gym.service.EmailService;
import com.gym.security.JwtUtil;
import com.gym.security.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")  // Be more specific in production
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @GetMapping("/all")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            logger.info("Registration request received for email: {}", user.getEmail());
            User registeredUser = userService.register(user);
            logger.info("User registered successfully with ID: {}", registeredUser.getId());
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginData) {
        try {
            logger.info("Login attempt for email: {}", loginData.getEmail());
            
            // Authenticate using Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword())
            );

            // Get user details to return to frontend and for JWT claims
            Optional<User> userOptional = userService.login(loginData.getEmail(), loginData.getPassword());
            if(userOptional.isPresent()) {
                User user = userOptional.get();
                
                // Generate JWT Token
                final UserDetails userDetails = userDetailsService.loadUserByUsername(loginData.getEmail());
                final String jwt = jwtUtil.generateToken(userDetails, user.getId(), user.getRole());

                Map<String, Object> response = new HashMap<>();
                response.put("token", jwt);
                response.put("id", user.getId());
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                response.put("role", user.getRole());
                response.put("phone", user.getPhone());
                
                logger.info("Login successful for user: {}", loginData.getEmail());
                
                // Send email notification asynchronously
                emailService.sendLoginNotification(user.getEmail(), user.getName());
                
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage(), e);
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/update/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@securityService.isOwner(#id)")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User user) {
        try {
            logger.info("Update request received for user ID: {}", id);
            User updatedUser = userService.updateUser(id, user);
            
            if (updatedUser != null) {
                logger.info("User updated successfully: {}", id);
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            } else {
                logger.warn("User not found for update: {}", id);
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Update error for user {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@securityService.isOwner(#id)")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            logger.info("Delete request received for user ID: {}", id);
            boolean result = userService.deleteUser(id);
            
            if (result) {
                logger.info("User deleted successfully: {}", id);
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            } else {
                logger.warn("User not found for deletion: {}", id);
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Delete error for user {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        try {
            logger.info("Checking if email exists: {}", email);
            boolean exists = userService.emailExists(email);
            return new ResponseEntity<>(exists, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error checking email: {}", e.getMessage(), e);
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@org.springframework.web.bind.annotation.RequestParam String email) {
        try {
            logger.info("Forgot password request for email: {}", email);
            userService.resetPassword(email);
            return new ResponseEntity<>(java.util.Map.of("message", "Password reset email sent"), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Forgot password error: {}", e.getMessage(), e);
            return new ResponseEntity<>(java.util.Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}