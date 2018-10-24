package ru.service;

import org.springframework.core.io.ByteArrayResource;

import javax.mail.MessagingException;

public interface MailService {
    void send(String to, String subject, String text, String from) throws MessagingException;
    void send(String[] to, String subject, String text, String from) throws MessagingException;
    void send(String to, String subject, String text, String from, String attachmentName, ByteArrayResource attachment) throws MessagingException;
    void send(String[] to, String subject, String text, String from, String attachmentName, ByteArrayResource attachment) throws MessagingException;
}