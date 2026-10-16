package kz.nurs.study.kafkaworkshopnotificationservice.kafka;

import kz.nurs.study.kafkaworkshopnotificationservice.dto.CreditDecisionDto;
import kz.nurs.study.kafkaworkshopnotificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Бизнес-обработка события из Kafka: отправляем уведомление.
 * Та же логика, что и для REST — меняется только способ, которым решение к нам пришло.
 */
@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {

    private final NotificationService notificationService;

    @Override
    public void processCreditDecision(CreditDecisionDto decision) {
        notificationService.sendNotification(decision, "KAFKA");
    }
}
