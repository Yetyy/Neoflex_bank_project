package neoflex.gateway.controller;

import lombok.RequiredArgsConstructor;
import neoflex.dto.EmailMessage;
import neoflex.dto.FinishRegistrationRequestDto;
import neoflex.dto.LoanOfferDto;
import neoflex.dto.LoanStatementRequestDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import neoflex.gateway.service.GatewayService;

import java.util.List;

@RestController
@RequestMapping("/api/loan")
@RequiredArgsConstructor
public class GatewayController {

    private final GatewayService gatewayService;

    @PostMapping("/statement")
    public ResponseEntity<List<LoanOfferDto>> createStatement(@RequestBody LoanStatementRequestDto requestDto) {
        return gatewayService.forwardRequest("/statement", requestDto, new ParameterizedTypeReference<List<LoanOfferDto>>() {});
    }

    @PostMapping("/offer")
    public ResponseEntity<Void> selectOffer(@RequestBody LoanOfferDto offer) {
        return gatewayService.forwardRequest("/statement/offer", offer, Void.class);
    }

    @PostMapping("/calculate/{statementId}")
    public ResponseEntity<Void> calculate(@PathVariable String statementId, @RequestBody FinishRegistrationRequestDto request) {
        return gatewayService.forwardRequest("/calculate/" + statementId, request, Void.class);
    }

    @PostMapping("/document/{statementId}/send")
    public ResponseEntity<Void> sendDocuments(@PathVariable String statementId) {
        return gatewayService.forwardRequest("/document/" + statementId + "/send", null, Void.class);
    }

    @PostMapping("/document/{statementId}/sign")
    public ResponseEntity<Void> signDocuments(@PathVariable String statementId) {
        return gatewayService.forwardRequest("/document/" + statementId + "/sign", null, Void.class);
    }

    @PostMapping("/document/{statementId}/code")
    public ResponseEntity<Void> codeDocuments(@PathVariable String statementId, @RequestBody String sesCode) {
        return gatewayService.forwardRequest("/document/" + statementId + "/code", sesCode, Void.class);
    }

}
