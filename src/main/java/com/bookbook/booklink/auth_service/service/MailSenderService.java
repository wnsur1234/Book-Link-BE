package com.bookbook.booklink.auth_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailSenderService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:#{null}}")
    private String from; // 없으면 username 기본 사용

    public void sendPlainText(String toEmail, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject(subject);
        msg.setText(text);
        if (from != null) {
            msg.setFrom(from);
        }
        try {
            mailSender.send(msg);
        } catch (RuntimeException e) {
            log.warn("Mail send failed to={}, subject={}", toEmail, subject, e);
            throw new RuntimeException("메일 전송 실패", e);
        }
    }

}
