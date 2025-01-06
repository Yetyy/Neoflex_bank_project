package neoflex.dto;

import lombok.*;
import neoflex.enums.Theme;

import java.util.UUID;

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

    public EmailMessage(UUID statementId, Theme theme, String address) {
        this.statementId = statementId;
        this.theme = theme;
        this.address = address;
    }

    public EmailMessage(String statementId, Theme theme, String address) {
        this.statementId = UUID.fromString(statementId);
        this.theme = theme;
        this.address = address;
    }
}
