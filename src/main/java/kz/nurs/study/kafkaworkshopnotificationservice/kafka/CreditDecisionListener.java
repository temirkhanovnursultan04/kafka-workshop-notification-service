package kz.nurs.study.kafkaworkshopnotificationservice.kafka;

import kz.nurs.study.kafkaworkshopnotificationservice.dto.CreditDecisionDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * АСИНХРОННЫЙ вход: слушаем топик. Никто нас не ждёт — забираем сообщения, когда готовы.
 * Если сервис был выключен, при старте он дочитает всё, что накопилось в топике.
 */
@Component
@RequiredArgsConstructor
public class CreditDecisionListener {

    private final CreditDecisionHandler creditDecisionHandler;

    @KafkaListener(topics = "${app.kafka-topics.credit-topic.name}")
    public void listen(ConsumerRecord<String, CreditDecisionDto> record) {
        creditDecisionHandler.handle(record);
    }
}
