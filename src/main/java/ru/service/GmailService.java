package ru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class GmailService implements MailService {

    @Autowired
    private JavaMailSender sender;

    @Value("${mail-from}")
    private String from;


    @Override
    public void send(String to, String subject, String text) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setText(text);
        helper.setSubject(subject);
        if (!to.equals("test@test.test")) sender.send(message);
    }

    @Override
    public void send(String[] to, String subject, String text) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setText(text);
        helper.setSubject(subject);
        sender.send(message);
    }

    @Override
    public void send(String to, String subject, String text, String attachmentName, ByteArrayResource attachment) throws MessagingException{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        helper.addAttachment(attachmentName, attachment);
        if (!to.equals("test@test.test")) sender.send(message);
    }

    @Override
    public void send(String[] to, String subject, String text, String attachmentName, ByteArrayResource attachment) throws MessagingException{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        helper.addAttachment(attachmentName, attachment);
        sender.send(message);
    }
}
