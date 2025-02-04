package neoflex.dossier.service;

import neoflex.dto.EmailMessage;
import neoflex.enums.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;


/**
 * Сервис для отправки email.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    @Value("${mail.from}")
    private String mailFrom;

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
        String link = "http://localhost:5173/loan/" + emailMessage.getStatementId();
        String text = "Завершите оформление. Ссылка: " + link;
        sendEmail(emailMessage, text);
    }

    /**
     * Отправляет письмо о создании документов.
     *
     * @param emailMessage сообщение email
     */
    public void sendCreateDocumentsEmail(EmailMessage emailMessage) {
        String link = "http://localhost:5173/loan/" + emailMessage.getStatementId() + "/document";
        String text = "Перейдите к оформлению документов. Ссылка: " + link;
        sendEmail(emailMessage, text);
    }

    /**
     * Отправляет письмо об отправке документов.
     *
     * @param emailMessage сообщение email
     */
    public void sendDocumentsEmail(EmailMessage emailMessage) {
        String link = "http://localhost:5173/loan/" + emailMessage.getStatementId() + "/document/sign";
        sendEmailWithAttachment(emailMessage, "Отправлены документы. Ссылка: " + link);
    }

    /**
     * Отправляет письмо об отправке документов на подписание.
     *
     * @param emailMessage сообщение email
     */
    public void sendSesEmail(EmailMessage emailMessage) {
        String link = "http://localhost:5173/loan/" + emailMessage.getStatementId() + "/code";
        String text = emailMessage.getText() + "\nСсылка для подтверждения: " + link;
        sendEmail(emailMessage, text);
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
        logger.info("Подготавливаем письмо");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailFrom);
        mailMessage.setTo(emailMessage.getAddress());
        mailMessage.setSubject(emailMessage.getTheme().toString());
        mailMessage.setText(text);

        try {
            mailSender.send(mailMessage);
            logger.info("Письмо отправлено");
        } catch (Exception e) {
            logger.error("Ошибка при отправке письма: {}", emailMessage, e);
        }
    }
    private void sendEmailWithAttachment(EmailMessage emailMessage, String text) {
        logger.info("Подготавливаем письмо с вложением");
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(emailMessage.getAddress());
            helper.setSubject(emailMessage.getTheme().toString());
            helper.setText(text);

            if (emailMessage.getPdfDocumentBytes() != null) {
                DataSource dataSource = new ByteArrayDataSource(emailMessage.getPdfDocumentBytes(), "application/pdf");
                helper.addAttachment("credit_agreement.pdf", dataSource);
            }

            mailSender.send(mimeMessage);
            logger.info("Письмо с вложением отправлено");
        } catch (MessagingException e) {
            logger.error("Ошибка при отправке письма с вложением: {}", emailMessage, e);
        }
    }
}
