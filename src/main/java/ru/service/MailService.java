package ru.service;

import org.springframework.core.io.ByteArrayResource;

import javax.mail.MessagingException;

public interface MailService {
    /**
     * @param to E-mail address that conforms RFC 5322 convention
     * @param subject String containing the subject of E-mail message. You could also call it "Header"
     * @param text Substance of E-mail message. Could contain HTML.
     * @throws MessagingException thrown when connection is lost/timed out, or there're problems with E-mail server
     */
    void send(String to, String subject, String text) throws MessagingException;

    /**
     * @param to An array of E-mail addresses that conform RFC 5322 convention
     * @param subject String containing the subject of E-mail message. You could also call it "Header"
     * @param text Substance of E-mail message. Could contain HTML.
     * @throws MessagingException thrown when connection is lost/timed out, or there're problems with E-mail server
     */
    void send(String[] to, String subject, String text) throws MessagingException;

    /**
     * @param to E-mail address that conforms RFC 5322 convention
     * @param subject String containing the subject of E-mail message. You could also call it "Header"
     * @param text Substance of E-mail message. Could contain HTML.
     * @param attachmentName A name for file that is attachment. Should also contain file extension.
     * @param attachment File that will be attached to the E-mail message
     * @throws MessagingException thrown when connection is lost/timed out, or there're problems with E-mail server
     */
    void send(String to, String subject, String text,  String attachmentName, ByteArrayResource attachment) throws MessagingException;
    /**
     * @param to An array of E-mail addresses that conform RFC 5322 convention
     * @param subject String containing the subject of E-mail message. You could also call it "Header"
     * @param text Substance of E-mail message. Could contain HTML.
     * @param attachmentName A name for file that is attachment. Should also contain file extension.
     * @param attachment File that will be attached to the E-mail message
     * @throws MessagingException thrown when connection is lost/timed out, or there're problems with E-mail server
     */
    void send(String[] to, String subject, String text, String attachmentName, ByteArrayResource attachment) throws MessagingException;
}
