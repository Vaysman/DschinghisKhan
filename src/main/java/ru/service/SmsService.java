package ru.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Base64;


@Service
public class SmsService {
    @Value("${sms-service-login}")
    private String login;
    @Value("${sms-service-password}")
    private String password;
    @Value("${sms-service-sender}")
    private String sender;


    public void sms(String phone, String text) throws MalformedURLException, IOException, IllegalArgumentException {

        String authString = login + ":" + password;
        String authStringEnc = Base64.getEncoder().encodeToString(authString.getBytes());

        URL url = new URL("http", "api.smsfeedback.ru", 80, "/messages/v2/send/?phone=%2B" + phone + "&text=" + URLEncoder.encode(text, "UTF-8") + "&sender=" + sender);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Authorization", authStringEnc);
        InputStream is = urlConnection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);

        int numCharsRead;
        char[] charArray = new char[1024];
        StringBuffer sb = new StringBuffer();
        while ((numCharsRead = isr.read(charArray)) > 0) {
            sb.append(charArray, 0, numCharsRead);
        }
        String result = sb.toString();


        if(result.contains("error")){
            if (result.contains("invalid mobile phone")){
                throw new IllegalArgumentException("Неверный номер телефона");
            }
        }
        System.out.println(result);

    }


}