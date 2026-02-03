package com.Group18.hotel_automation.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendStaffCredentials(String toEmail, String name, String password) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Hotel Automation Account");
        message.setText(
                "Hello " + name + ",\n\n" +
                        "Your staff account has been created.\n\n" +
                        "Login Credentials:\n" +
                        "Email: " + toEmail + "\n" +
                        "Temporary Password: " + password + "\n\n" +
                        "Please change your password after first login.\n\n" +
                        "Regards,\nHotel Management"
        );

        mailSender.send(message);
    }
}
