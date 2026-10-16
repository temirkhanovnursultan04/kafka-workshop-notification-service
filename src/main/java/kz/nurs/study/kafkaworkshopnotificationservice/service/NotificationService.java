package kz.nurs.study.kafkaworkshopnotificationservice.service;

import kz.nurs.study.kafkaworkshopnotificationservice.dto.CreditDecisionDto;

public interface NotificationService {

    /**
     * @param channel откуда пришло решение: REST или KAFKA — для логов и текста уведомления
     */
    void sendNotification(CreditDecisionDto decision, String channel);
}
