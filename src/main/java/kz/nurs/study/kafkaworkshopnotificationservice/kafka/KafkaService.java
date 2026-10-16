package kz.nurs.study.kafkaworkshopnotificationservice.kafka;

import kz.nurs.study.kafkaworkshopnotificationservice.dto.CreditDecisionDto;

public interface KafkaService {

    void processCreditDecision(CreditDecisionDto decision);
}
