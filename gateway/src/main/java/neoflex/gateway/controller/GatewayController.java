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
@RequestMapping("/")
@RequiredArgsConstructor
public class GatewayController {

    private final GatewayService gatewayService;

    @PostMapping("/application")
    public ResponseEntity<List<LoanOfferDto>> createStatement(@RequestBody LoanStatementRequestDto requestDto) {
        return gatewayService.forwardRequest("/statement", requestDto, new ParameterizedTypeReference<List<LoanOfferDto>>() {});
    }

    @PostMapping("/application/apply")
    public ResponseEntity<Void> selectOffer(@RequestBody LoanOfferDto offer) {
        return gatewayService.forwardRequest("/statement/offer", offer, Void.class);
    }

    @PutMapping("/application/registration/{applicationId}")
    public ResponseEntity<Void> calculate(@PathVariable String applicationId, @RequestBody FinishRegistrationRequestDto request) {
        return gatewayService.forwardRequest("/deal/calculate/" + applicationId, request, Void.class);
    }

    @GetMapping("/admin/application/{applicationId}")
    public ResponseEntity<?> getApplication(@PathVariable String applicationId) {
        return gatewayService.forwardRequest("/deal/admin/statement/" + applicationId, null, Object.class);
    }

    @PostMapping("/document/{applicationId}")
    public ResponseEntity<Void> sendDocuments(@PathVariable String applicationId) {
        return gatewayService.forwardRequest("/deal/document/" + applicationId + "/send", null, Void.class);
    }

    @PostMapping("/document/{applicationId}/sign")
    public ResponseEntity<Void> signDocuments(@PathVariable String applicationId) {
        return gatewayService.forwardRequest("/deal/document/" + applicationId + "/sign", null, Void.class);
    }

    @PostMapping("/document/{applicationId}/sign/code")
    public ResponseEntity<Void> codeDocuments(@PathVariable String applicationId, @RequestBody String sesCode) {
        return gatewayService.forwardRequest("/deal/document/" + applicationId + "/code", sesCode, Void.class);
    }

    @PostMapping("/application/{applicationId}/deny")
    public ResponseEntity<Void> denyApplication(@PathVariable String applicationId) {
        return gatewayService.forwardRequest("/deal/admin/statement/" + applicationId + "/status", null, Void.class);
    }

    @PostMapping("/email")
    public ResponseEntity<Void> subscribeNews(@RequestBody String email) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress(email);
//        emailMessage.setTheme("SUBSCRIBE_NEWS");
        return gatewayService.forwardRequest("/api/dossier/send-email", emailMessage, Void.class);
    }
}
