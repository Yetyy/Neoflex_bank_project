package neoflex.deal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Конфигурация для WebClient.
 */
@Configuration
public class WebClientConfig {

    @Value("${calculator.url}")
    private String calculatorServiceUrl;

    /**
     * Создает и настраивает WebClient для взаимодействия с микросервисом Калькулятор.
     *
     * @return настроенный WebClient
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(calculatorServiceUrl)
                .build();
    }
}
