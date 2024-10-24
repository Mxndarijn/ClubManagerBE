package nl.shootingclub.clubmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import nl.shootingclub.clubmanager.configuration.data.HTMLTemplate;
import nl.shootingclub.clubmanager.configuration.data.Language;
import nl.shootingclub.clubmanager.configuration.data.json.EmailData;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendHTMLMail(String email, HTMLTemplate htmlTemplate, Language language, HashMap<String, String> placeholders) throws MessagingException {

        new Thread(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();

                message.setFrom(new InternetAddress("info@ledencentraal.nl"));
                message.setRecipients(MimeMessage.RecipientType.TO, email);
                AtomicReference<String> template = new AtomicReference<>(loadFileAsString(htmlTemplate.getLocation(language) + ".html"));
                placeholders.forEach((k,v) -> {
                    template.set(template.get().replaceAll("%%" + k + "%%", v));
                });
                String jsonContent = loadFileAsString(htmlTemplate.getLocation(language) + ".json");
                ObjectMapper objectMapper = new ObjectMapper();
                EmailData data = objectMapper.readValue(jsonContent, EmailData.class);


                message.setSubject(data.getSubject());
                message.setContent(template.get(), "text/html; charset=utf-8");

                mailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
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