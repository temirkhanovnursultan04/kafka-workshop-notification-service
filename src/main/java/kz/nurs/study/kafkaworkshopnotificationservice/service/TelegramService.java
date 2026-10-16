package kz.nurs.study.kafkaworkshopnotificationservice.service;

public interface TelegramService {

    void sendMessage(String text);
}
