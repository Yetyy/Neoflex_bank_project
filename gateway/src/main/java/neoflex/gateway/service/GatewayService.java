package neoflex.gateway.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Сервис для перенаправления запросов на другие микросервисы.
 */
@Service
@RequiredArgsConstructor
public class GatewayService {

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(GatewayService.class);

    @Value("${STATEMENT_URL}")
    private String statementUrl;
    @Value("${DEAL_URL}")
    private String dealUrl;
    @Value("${DOSSIER_URL}")
    private String dossierUrl;
    /**
     * Перенаправляет запрос на указанный URL с заданным телом и типом ответа.
     *
     * @param path         путь запроса
     * @param requestBody  тело запроса
     * @param responseType тип ответа
     * @param <T>          тип тела запроса
     * @param <R>          тип ответа
     * @return  ответ от микросервиса
     * @throws RuntimeException если происходит ошибка при перенаправлении запроса
     */
    public <T, R> ResponseEntity<R> forwardRequest(HttpMethod method, String path, T requestBody, Class<R> responseType) {
        String url = getUrl(path);
        logger.info("Перенаправление запроса на URL: {}, с телом: {}", url, requestBody);

        try {
            Mono<ResponseEntity<R>> responseMono = webClient.method(method)
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.justOrEmpty(requestBody), requestBody != null ? requestBody.getClass() : Object.class)
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.isError(), clientResponse -> {
                        logger.error("Ошибка ответа от URL: {}, статус: {}", url, clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Ошибка от сервиса: " + clientResponse.statusCode() + ", тело: " + errorBody)));
                    })
                    .toEntity(responseType)
                    .doOnError(e -> logger.error("Ошибка во время запроса к URL: {}, ошибка: {}", url, e.getMessage()));

            ResponseEntity<R> response = responseMono.block();
            logger.info("Ответ от URL: {}, статус: {}, тело: {}", url, response.getStatusCode(), response.getBody());
            return response;

        } catch (Exception e) {
            logger.error("Ошибка перенаправления запроса на URL: {}, ошибка: {}", url, e.getMessage());
            throw new RuntimeException("Ошибка перенаправления запроса: " + e.getMessage());
        }
    }
    /**
     * Перенаправляет запрос на указанный URL с заданным телом и параметризованным типом ответа.
     *
     * @param path         путь запроса
     * @param requestBody  тело запроса
     * @param responseType параметризованный тип ответа
     * @param <T>          тип тела запроса
     * @param <R>          тип ответа
     * @return  ответ от микросервиса
     * @throws RuntimeException если происходит ошибка при перенаправлении запроса
     */
    public <T, R> ResponseEntity<R> forwardRequest(HttpMethod method, String path, T requestBody, ParameterizedTypeReference<R> responseType) {
        String url = getUrl(path);
        logger.info("Перенаправление запроса на URL: {}, с телом: {}", url, requestBody);

        try {
            Mono<ResponseEntity<R>> responseMono = webClient.method(method)
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.justOrEmpty(requestBody), requestBody != null ? requestBody.getClass() : Object.class)
                    .retrieve()
                    .onStatus(httpStatus -> httpStatus.isError(), clientResponse -> {
                        logger.error("Ошибка ответа от URL: {}, статус: {}", url, clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Ошибка от сервиса: " + clientResponse.statusCode() + ", тело: " + errorBody)));
                    })
                    .toEntity(responseType)
                    .doOnError(e -> logger.error("Ошибка во время запроса к URL: {}, ошибка: {}", url, e.getMessage()));

            ResponseEntity<R> response = responseMono.block();
            logger.info("Ответ от URL: {}, статус: {}, тело: {}", url, response.getStatusCode(), response.getBody());
            return response;

        } catch (Exception e) {
            logger.error("Ошибка перенаправления запроса на URL: {}, ошибка: {}", url, e.getMessage());
            throw new RuntimeException("Ошибка перенаправления запроса: " + e.getMessage());
        }
    }


    /**
     * Определяет URL микросервиса на основе пути запроса.
     *
     * @param path путь запроса
     * @return URL микросервиса
     * @throws IllegalArgumentException если путь неизвестен
     */
    private String getUrl(String path) {
        if (path.startsWith("/statement")) {
            return statementUrl + path;
        } else if (path.startsWith("/calculate") || path.startsWith("/document") || path.startsWith("")) {
            return dealUrl + path;
        } else {
            throw new IllegalArgumentException("Неизвестный путь: " + path);
        }
    }


}
