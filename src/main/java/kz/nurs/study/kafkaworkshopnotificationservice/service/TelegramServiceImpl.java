package kz.nurs.study.kafkaworkshopnotificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Отправка сообщения в Telegram-канал через Bot API (обычный HTTP POST).
 * Если токен не задан — просто пишем в лог, демо не ломается.
 */
@Slf4j
@Service
public class TelegramServiceImpl implements TelegramService {

    private final RestTemplate telegramRestTemplate;
    private final String botToken;
    private final String chatId;

    public TelegramServiceImpl(RestTemplate telegramRestTemplate,
                               @Value("${app.telegram.bot-token}") String botToken,
                               @Value("${app.telegram.chat-id}") String chatId) {
        this.telegramRestTemplate = telegramRestTemplate;
        this.botToken = botToken;
        this.chatId = chatId;
    }

    @Override
    public void sendMessage(String text) {
        if (botToken.isBlank() || chatId.isBlank()) {
            log.info("[TELEGRAM] Токен или chat-id не заданы — сообщение только в логе");
            return;
        }
        try {
            telegramRestTemplate.postForObject(
                    "/bot{token}/sendMessage",
                    Map.of("chat_id", chatId, "text", text, "parse_mode", "HTML"),
                    String.class,
                    botToken);
            log.info("[TELEGRAM] Сообщение отправлено в канал");
        } catch (Exception e) {
            // В тексте исключения может быть URL с токеном — его на экран не выводим
            log.warn("[TELEGRAM] Не удалось отправить сообщение: {}", e.getClass().getSimpleName());
        }
    }
}
