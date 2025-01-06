package neoflex.dossier.config;

import neoflex.dto.EmailMessage;
import neoflex.dossier.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Конфигурация для Kafka Listener.
 */
@Service
public class KafkaListenerConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaListenerConfig.class);

    private final EmailService emailService;

    @Autowired
    public KafkaListenerConfig(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Слушает сообщения из Kafka и обрабатывает их.
     *
     * @param emailMessage сообщение email
     */
    @KafkaListener(topics = {"finish-registration", "create-documents", "send-documents", "send-ses", "credit-issued", "statement-denied", "sign-documents", "code-documents"}, groupId = "dossier-group")
    public void listen(EmailMessage emailMessage) {
        logger.info("Получено сообщение: {}", emailMessage);

        try {
            processEmailMessage(emailMessage);
        } catch (Exception e) {
            logger.error("Ошибка при обработке сообщения: {}", emailMessage, e);
            // Отправка сообщения в DLT
        }
    }

    /**
     * Обрабатывает сообщение email в зависимости от его темы.
     *
     * @param emailMessage сообщение email
     */
    private void processEmailMessage(EmailMessage emailMessage) {
        switch (emailMessage.getTheme()) {
            case FINISH_REGISTRATION:
                emailService.sendFinishRegistrationEmail(emailMessage);
                break;
            case CREATE_DOCUMENTS:
                emailService.sendCreateDocumentsEmail(emailMessage);
                break;
            case SEND_DOCUMENTS:
                emailService.sendDocumentsEmail(emailMessage);
                break;
            case SEND_SES:
                emailService.sendSesEmail(emailMessage);
                break;
            case CREDIT_ISSUED:
                emailService.sendCreditIssuedEmail(emailMessage);
                break;
            case STATEMENT_DENIED:
                emailService.sendStatementDeniedEmail(emailMessage);
                break;
            case SIGN_DOCUMENTS:
                emailService.sendSignDocumentsEmail(emailMessage);
                break;
            case CODE_DOCUMENTS:
                emailService.sendCodeDocumentsEmail(emailMessage);
                break;
            default:
                logger.warn("Неизвестная тема письма: {}", emailMessage.getTheme());
                throw new IllegalArgumentException("Неизвестная тема письма: " + emailMessage.getTheme());
        }
    }
}
