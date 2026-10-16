package kz.nurs.study.kafkaworkshopnotificationservice.kafka;

import kz.nurs.study.kafkaworkshopnotificationservice.dto.CreditDecisionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

/**
 * Разбирает «конверт» Kafka: откуда пришло сообщение (партиция, offset, ключ),
 * и передаёт само содержимое дальше в KafkaService.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CreditDecisionHandler {

    private final KafkaService kafkaService;

    public void handle(ConsumerRecord<String, CreditDecisionDto> record) {
        log.info("[KAFKA] Новое сообщение: topic={}, partition={}, offset={}, key={}",
                record.topic(), record.partition(), record.offset(), record.key());

        CreditDecisionDto decision = record.value();
        if (decision == null) {
            log.warn("[KAFKA] Пустое сообщение — пропускаем");
            return;
        }
        kafkaService.processCreditDecision(decision);
    }
}
