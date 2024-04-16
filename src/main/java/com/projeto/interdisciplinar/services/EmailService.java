package com.projeto.interdisciplinar.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            String htmlMsg = "<html><body>" +
                    "<h2 style=\"color: #007bff;\">Confirme seu e-mail</h2>" +
                    "<p style=\"font-size: 18px;\">" + body + "</p>" +
                    "</body></html>";

            helper.setText(htmlMsg, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setFrom("${spring.mail.username}");

            mailSender.send(message);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
