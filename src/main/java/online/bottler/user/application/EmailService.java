package online.bottler.user.application;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.shared.exception.ApplicationException;
import online.bottler.user.application.port.in.EmailUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class EmailService implements EmailUseCase {

    private final JavaMailSender emailSender;

    @Value("${mail.username}")
    private String username;

    public void sendEmail(String toEmail, String title, String content) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject(title);
        helper.setText(content, true);
        helper.setReplyTo(username);
        try {
            emailSender.send(message);
        } catch (RuntimeException e) {
            throw new ApplicationException("이메일 인증 요청에 실패했습니다.");
        }
    }
}
