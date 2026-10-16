package kz.nurs.study.kafkaworkshopnotificationservice.kafka;

import kz.nurs.study.kafkaworkshopnotificationservice.dto.CreditDecisionDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Kafka для notification-service. Сервис только ЧИТАЕТ события (consumer).
 * bootstrap-servers, group-id, auto-offset-reset берутся из application.yaml.
 */
@Slf4j
@Configuration
public class KafkaConsumerConfig {

    /**
     * Фабрика consumer'ов: ключ — строка, значение — JSON -> CreditDecisionDto.
     * ErrorHandlingDeserializer: «битое» сообщение не роняет consumer, а уходит в обработчик ошибок.
     */
    @Bean
    public ConsumerFactory<String, CreditDecisionDto> consumerFactory(KafkaProperties kafkaProperties) {
        JacksonJsonDeserializer<CreditDecisionDto> jsonDeserializer =
                new JacksonJsonDeserializer<>(CreditDecisionDto.class, false); // тип знаем сами, заголовки не нужны
        return new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(),
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer));
    }

    /**
     * Обработчик ошибок: если обработка упала — повторяем N раз с паузой,
     * потом логируем и пропускаем сообщение, чтобы не застрять на нём навсегда.
     */
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            @Value("${app.kafka-topics.backoff-interval}") long backoffInterval,
            @Value("${app.kafka-topics.backoff-attempts}") long backoffAttempts) {
        return new DefaultErrorHandler(
                (record, ex) -> log.error("[KAFKA] Сообщение пропущено после {} попыток: topic={}, offset={}, ошибка={}",
                        backoffAttempts, record.topic(), record.offset(), ex.getMessage()),
                new FixedBackOff(backoffInterval, backoffAttempts));
    }

    /** Контейнер, который запускает методы с @KafkaListener. */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CreditDecisionDto> kafkaListenerContainerFactory(
            ConsumerFactory<String, CreditDecisionDto> consumerFactory,
            DefaultErrorHandler kafkaErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, CreditDecisionDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        return factory;
    }
}
