package neoflex.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AuthResponseDto {
    private final String token;

    @JsonCreator
    public AuthResponseDto(@JsonProperty("token") String token) {
        this.token = token;
    }
}