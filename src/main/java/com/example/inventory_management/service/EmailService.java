package com.example.inventory_management.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void sendLowStockAlert(String productName, int currentStock, String employeeEmail) {
        String subject = "Low Stock Alert for " + productName;
        String message = "The stock for " + productName + " has dropped to " + currentStock + 
                         ". Please restock it as soon as possible.";

        try {
            sendEmail(employeeEmail, subject, message);  // Corrected variable names
        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + employeeEmail + ": " + e.getMessage());
        }
    }

    private void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, false);  // `false` for plain text, change to `true` if using HTML

        mailSender.send(message);
    }
}
