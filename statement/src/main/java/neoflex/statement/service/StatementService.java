package neoflex.statement.service;

import neoflex.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class StatementService {

    private static final Logger logger = LoggerFactory.getLogger(StatementService.class);

    @Autowired
    private WebClient webClient;

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto requestDto) {
        logger.info("Received loan statement request: {}", requestDto);

        // Отправляем запрос в микросервис deal
        List<LoanOfferDto> loanOffers = webClient.post()
                .uri("/deal/statement")
                .body(Mono.just(requestDto), LoanStatementRequestDto.class)
                .retrieve()
                .bodyToFlux(LoanOfferDto.class)
                .collectList()
                .block();

        logger.info("Received loan offers: {}", loanOffers);
        return loanOffers;
    }

    public void selectLoanOffer(LoanOfferDto offerDto) {
        logger.info("Received loan offer selection: {}", offerDto);

        // Отправляем запрос в микросервис deal
        webClient.post()
                .uri("/deal/offer/select")
                .body(Mono.just(offerDto), LoanOfferDto.class)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        logger.info("Loan offer selected successfully");
    }
}
