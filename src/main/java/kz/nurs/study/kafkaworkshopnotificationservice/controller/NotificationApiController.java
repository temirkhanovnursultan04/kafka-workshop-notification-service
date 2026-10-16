package kz.nurs.study.kafkaworkshopnotificationservice.controller;

import kz.nurs.study.kafkaworkshopnotificationservice.dto.CreditDecisionDto;
import kz.nurs.study.kafkaworkshopnotificationservice.dto.NotificationResponseDto;
import kz.nurs.study.kafkaworkshopnotificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * СИНХРОННЫЙ вход: credit-service вызывает этот эндпоинт и ЖДЁТ, пока мы ответим.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponseDto> sendNotification(@RequestBody CreditDecisionDto decision) {
        notificationService.sendNotification(decision, "REST");
        return ResponseEntity.ok(new NotificationResponseDto("SENT",
                "Уведомление для %s %s отправлено".formatted(decision.getName(), decision.getSurname())));
    }
}
