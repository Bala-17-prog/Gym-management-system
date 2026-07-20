package com.gym.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendLoginNotification(String toEmail, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("New Login Alert - Power Gym");

            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            String htmlMsg = "<div style='font-family: Arial, sans-serif; padding: 20px; color: #333;'>"
                    + "<h2 style='color: #007bff;'>Hello " + name + ",</h2>"
                    + "<p>We detected a new login to your Power Gym account on <strong>" + time + "</strong>.</p>"
                    + "<p>If this was you, you can safely ignore this email.</p>"
                    + "<p style='color: red;'>If you did not authorize this login, please contact support immediately.</p>"
                    + "<hr>"
                    + "<p style='font-size: 12px; color: #777;'>Power Gym Administration</p>"
                    + "</div>";

            helper.setText(htmlMsg, true);

            mailSender.send(message);
            logger.info("Login notification email sent to: {}", toEmail);

        } catch (MessagingException e) {
            logger.error("Failed to send login email to: {}", toEmail, e);
        } catch (Exception e) {
            logger.error("Error sending email: {}", e.getMessage());
        }
    }
    @Async
    public void sendPasswordResetEmail(String toEmail, String name, String newPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject("Password Reset - Power Gym");
            
            String htmlMsg = "<div style='font-family: Arial, sans-serif; padding: 20px; color: #333;'>"
                    + "<h2 style='color: #007bff;'>Hello " + name + ",</h2>"
                    + "<p>You requested a password reset for your Power Gym account.</p>"
                    + "<p>Your new temporary password is: <strong><span style='background-color:#f0f0f0; padding:5px 10px; border-radius:5px; font-size:18px;'>" + newPassword + "</span></strong></p>"
                    + "<p>Please login using this temporary password and then update it in your profile settings as soon as possible.</p>"
                    + "<hr>"
                    + "<p style='font-size: 12px; color: #777;'>Power Gym Administration</p>"
                    + "</div>";

            helper.setText(htmlMsg, true);

            mailSender.send(message);
            logger.info("Password reset email sent to: {}", toEmail);

        } catch (MessagingException e) {
            logger.error("Failed to send password reset email to: {}", toEmail, e);
        } catch (Exception e) {
            logger.error("Error sending password reset email: {}", e.getMessage());
        }
    }
}
