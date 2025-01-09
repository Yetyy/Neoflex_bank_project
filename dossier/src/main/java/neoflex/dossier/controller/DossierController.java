package neoflex.dossier.controller;

import lombok.RequiredArgsConstructor;
import neoflex.dossier.service.EmailService;
import neoflex.dto.EmailMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для обработки запросов, связанных с досье.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dossier")
public class DossierController {

    private static final Logger logger = LoggerFactory.getLogger(DossierController.class);
    private final EmailService emailService;

    /**
     * Отправляет письмо клиенту.
     *
     * @param emailMessage сообщение email
     * @return ответ с результатом операции
     */
    @PostMapping("/send-email")
    @Operation(summary = "Отправка письма", description = "Отправляет письмо клиенту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция"),
            @ApiResponse(responseCode = "400", description = "Неверный ввод",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Void> sendEmail(@RequestBody EmailMessage emailMessage) {
        logger.info("Отправка письма: {}", emailMessage);

        try {
            processEmailMessage(emailMessage);
            logger.info("Письмо отправлено: {}", emailMessage);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Неизвестная тема письма: {}", emailMessage.getTheme());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Ошибка при отправке письма: {}", emailMessage, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
//            case SIGN_DOCUMENTS:
//                emailService.sendSignDocumentsEmail(emailMessage);
//                break;
//            case CODE_DOCUMENTS:
//                emailService.sendCodeDocumentsEmail(emailMessage);
//                break;
            default:
                logger.warn("Неизвестная тема письма: {}", emailMessage.getTheme());
                throw new IllegalArgumentException("Неизвестная тема письма: " + emailMessage.getTheme());
        }
    }
}
