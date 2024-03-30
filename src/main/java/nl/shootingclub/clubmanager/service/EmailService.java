package nl.shootingclub.clubmanager.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import nl.shootingclub.clubmanager.configuration.data.HTMLTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    public void sendHTMLMail(String email, HTMLTemplate htmlTemplate, HashMap<String, String> placeholders) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress("info@ledencentraal.nl"));
        message.setRecipients(MimeMessage.RecipientType.TO, email);
        AtomicReference<String> template = new AtomicReference<>(loadFileAsString(htmlTemplate.getLocation()));
        placeholders.forEach((k,v) -> {
            template.set(template.get().replaceAll(k, v));
        });
        System.out.println("Template: ");
        System.out.println(template.get());

        message.setSubject(htmlTemplate.getSubject());
        message.setContent(template.get(), "text/html; charset=utf-8");

        mailSender.send(message);

    }

    public String loadFileAsString(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
