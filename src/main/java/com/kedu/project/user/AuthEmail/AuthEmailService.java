package com.kedu.project.user.AuthEmail;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;

@Service
public class AuthEmailService {

    @Value("${mail.from.email}")
    private String fromEmail;
    @Value("${mail.app.password}")
    private String appPassword;
    String authCode = generateAuthCode();

    protected String doPost(String id)
            throws ServletException, IOException {
        String toEmail = URLDecoder.decode(id, StandardCharsets.UTF_8);
        ;

        try {
            sendEmail(toEmail, authCode);
            return authCode;
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateAuthCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    private void sendEmail(String toEmail, String authCode) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.naver.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication(fromEmail, appPassword);
            }
        });

        String htmlContent = String.format("""
                <p>이전 페이지로 이동하여 아래에 받으신 인증번호를 입력해 주세요.</p>
                <p>%s</p>
                <p>* 이메일 수정시 다시 인증해야합니다. *</p>
                """, authCode);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("코코벨 이메일 인증 확인", "UTF-8");
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        jakarta.mail.Transport.send(message);
    }
}
