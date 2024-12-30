package neoflex.statement.service;

import lombok.RequiredArgsConstructor;
import neoflex.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Сервис для обработки заявки на кредит.
 */
@Service
@RequiredArgsConstructor
public class StatementService {

    private static final Logger logger = LoggerFactory.getLogger(StatementService.class);

    private final WebClient webClient;

    /**
     * Получает предложения по кредиту на основе заявки.
     *
     * @param requestDto объект с данными заявки на кредит
     * @return список предложений по кредиту
     * @throws RuntimeException если произошла ошибка при получении предложений
     */
    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto requestDto) {
        logger.info("Получен запрос на прескоринг и расчет условий кредита: {}", requestDto);

        try {
            // Отправляем запрос в микросервис deal
            List<LoanOfferDto> loanOffers = webClient.post()
                    .uri("/deal/statement")
                    .body(Mono.just(requestDto), LoanStatementRequestDto.class)
                    .retrieve()
                    .bodyToFlux(LoanOfferDto.class)
                    .collectList()
                    .block();

            logger.info("Получены предложения по кредиту: {}", loanOffers);
            return loanOffers;
        } catch (Exception e) {
            logger.error("Ошибка при вызове микросервиса deal для прескоринга: {}", e.getMessage());
            throw new RuntimeException("Ошибка при получении предложений по кредиту", e);
        }
    }

    /**
     * Выбирает одно из предложений по кредиту.
     *
     * @param offerDto объект с данными выбранного кредитного предложения
     * @throws RuntimeException если произошла ошибка при выборе предложения
     */
    public void selectLoanOffer(LoanOfferDto offerDto) {
        logger.info("Получен запрос на выбор кредитного предложения: {}", offerDto);

        try {
            // Отправляем запрос в микросервис deal
            webClient.post()
                    .uri("/deal/offer/select")
                    .body(Mono.just(offerDto), LoanOfferDto.class)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            logger.info("Кредитное предложение успешно выбрано");
        } catch (Exception e) {
            logger.error("Ошибка при выборе кредитного предложения: {}", e.getMessage());
            throw new RuntimeException("Ошибка при выборе кредитного предложения", e);
        }
    }
}
