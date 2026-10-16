package kz.nurs.study.kafkaworkshopnotificationservice.service;

import kz.nurs.study.kafkaworkshopnotificationservice.dto.CreditDecisionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Общая логика уведомления — и для REST, и для Kafka.
 * Специально «медленная»: имитируем тяжёлую работу (шаблоны, рассылка, сторонние API).
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final TelegramService telegramService;
    private final long processingDelayMs;

    public NotificationServiceImpl(TelegramService telegramService,
                                   @Value("${app.notification.processing-delay-ms}") long processingDelayMs) {
        this.telegramService = telegramService;
        this.processingDelayMs = processingDelayMs;
    }

    @Override
    public void sendNotification(CreditDecisionDto decision, String channel) {
        log.info("[{}] Получено решение для {} {} — начинаем обработку ({} мс)...",
                channel, decision.getName(), decision.getSurname(), processingDelayMs);

        simulateHeavyWork();

        String text = buildText(decision, channel);
        log.info("[{}] Уведомление готово:\n{}", channel, text);
        telegramService.sendMessage(text);
    }

    private void simulateHeavyWork() {
        try {
            Thread.sleep(processingDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String buildText(CreditDecisionDto d, String channel) {
        boolean approved = Boolean.TRUE.equals(d.getIsApproved());
        return (approved ? "✅ Кредит одобрен" : "❌ В кредите отказано") + "\n"
                + "Клиент: " + escape(d.getName()) + " " + escape(d.getSurname()) + "\n"
                + "Сумма: " + d.getCreditSum() + " ₸ на " + d.getCreditPeriodInMonths() + " мес.\n"
                + "Платёж: " + d.getMonthlyPayment() + " ₸ в месяц\n"
                + "Канал: " + channel;
    }

    /** Имена вводят люди — экранируем, чтобы Telegram (parse_mode=HTML) не сломался. */
    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
