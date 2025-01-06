package neoflex.dossier.service;

import neoflex.dto.EmailMessage;
import neoflex.enums.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendFinishRegistrationEmail(EmailMessage emailMessage) {
        sendEmail(emailMessage, "Завершите оформление");
    }

    public void sendCreateDocumentsEmail(EmailMessage emailMessage) {
        sendEmail(emailMessage, "Созданы документы");
    }

    public void sendDocumentsEmail(EmailMessage emailMessage) {
        sendEmail(emailMessage, "Отправлены документы");
    }

    public void sendSesEmail(EmailMessage emailMessage) {
        sendEmail(emailMessage, "Отправлены документы на подписание");
    }

    public void sendCreditIssuedEmail(EmailMessage emailMessage) {
        sendEmail(emailMessage, "Кредит выдан");
    }

    public void sendStatementDeniedEmail(EmailMessage emailMessage) {
        sendEmail(emailMessage, "Заявка отклонена");
    }

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
}
