package kz.nurs.study.kafkaworkshopnotificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.Duration;

/**
 * RestTemplate для Telegram Bot API. В коде пишем только путь — /bot{token}/sendMessage.
 */
@Configuration
public class RestTemplateConfig {

    private static final String TELEGRAM_API_URL = "https://api.telegram.org";

    @Bean
    public RestTemplate telegramRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(TELEGRAM_API_URL));
        return restTemplate;
    }
}
