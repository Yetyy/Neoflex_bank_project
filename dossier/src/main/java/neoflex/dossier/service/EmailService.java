package neoflex.dossier.service;

import neoflex.dto.EmailMessage;
import neoflex.enums.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;

import java.util.Base64;

/**
 * Сервис для отправки email.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Отправляет письмо о завершении регистрации.
     *
     * @param emailMessage сообщение email
     */
    public void sendFinishRegistrationEmail(EmailMessage emailMessage) {
        sendEmail(emailMessage, "Завершите оформление");
    }

    /**
     * Отправляет письмо о создании документов.
     *
     * @param emailMessage сообщение email
     */
    public void sendCreateDocumentsEmail(EmailMessage emailMessage) {
        sendEmail(emailMessage, "Перейдите к оформлению документов");
    }

    /**
     * Отправляет письмо об отправке документов.
     *
     * @param emailMessage сообщение email
     */
    public void sendDocumentsEmail(EmailMessage emailMessage) {
        sendEmailWithAttachment(emailMessage, "Отправлены документы");
    }

    /**
     * Отправляет письмо об отправке документов на подписание.
     *
     * @param emailMessage сообщение email
     */
    public void sendSesEmail(EmailMessage emailMessage) {
        sendEmail(emailMessage, emailMessage.getText());
    }

    /**
     * Отправляет письмо о выдаче кредита.
     *
     * @param emailMessage сообщение email
     */
    public void sendCreditIssuedEmail(EmailMessage emailMessage) {
        sendEmail(emailMessage, "Кредит выдан");
    }

    /**
     * Отправляет письмо об отказе в заявке.
     *
     * @param emailMessage сообщение email
     */
    public void sendStatementDeniedEmail(EmailMessage emailMessage) {
        sendEmail(emailMessage, "Заявка отклонена");
    }

    /**
     * Отправляет письмо с указанным текстом.
     *
     * @param emailMessage сообщение email
     * @param text         текст письма
     */
    private void sendEmail(EmailMessage emailMessage, String text) {
        logger.info("Подготавливаем письмо: {}", emailMessage);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("bank_dossier@mail.ru");
        mailMessage.setTo(emailMessage.getAddress());
        mailMessage.setSubject(emailMessage.getTheme().toString());
        mailMessage.setText(text);

        try {
            mailSender.send(mailMessage);
            logger.info("Письмо отправлено: {}", emailMessage);
        } catch (Exception e) {
            logger.error("Ошибка при отправке письма: {}", emailMessage, e);
        }
    }
    private void sendEmailWithAttachment(EmailMessage emailMessage, String text) {
        logger.info("Подготавливаем письмо с вложением: {}", emailMessage);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("bank_dossier@mail.ru");
            helper.setTo(emailMessage.getAddress());
            helper.setSubject(emailMessage.getTheme().toString());
            helper.setText(text);

            if (emailMessage.getPdfDocumentBytes() != null) {
                DataSource dataSource = new ByteArrayDataSource(emailMessage.getPdfDocumentBytes(), "application/pdf");
                helper.addAttachment("credit_agreement.pdf", dataSource);
            }

            mailSender.send(mimeMessage);
            logger.info("Письмо с вложением отправлено: {}", emailMessage);
        } catch (MessagingException e) {
            logger.error("Ошибка при отправке письма с вложением: {}", emailMessage, e);
        }
    }
}
