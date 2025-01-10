package neoflex.dto;

import lombok.*;
import neoflex.enums.Theme;

import java.util.UUID;

/**
 * DTO для представления сообщения email.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {

    private String address;
    private Theme theme;
    private UUID statementId;
    private String text;
    private byte[] pdfDocumentBytes;


    /**
     * Создает новое сообщение email с указанными параметрами.
     *
     * @param statementId идентификатор заявки
     * @param theme        тема письма
     * @param address      адрес получателя
     */
    public EmailMessage(UUID statementId, Theme theme, String address) {
        this.statementId = statementId;
        this.theme = theme;
        this.address = address;
    }

    /**
     * Создает новое сообщение email с указанными параметрами.
     *
     * @param statementId идентификатор заявки в виде строки
     * @param theme        тема письма
     * @param address      адрес получателя
     */
    public EmailMessage(String statementId, Theme theme, String address) {
        this.statementId = UUID.fromString(statementId);
        this.theme = theme;
        this.address = address;
    }
    public EmailMessage(UUID statementId, Theme theme, String address, byte[] pdfDocumentBytes) {
        this.statementId = statementId;
        this.theme = theme;
        this.address = address;
        this.pdfDocumentBytes = pdfDocumentBytes;
    }
}
