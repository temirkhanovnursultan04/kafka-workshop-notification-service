package kz.nurs.study.kafkaworkshopnotificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ответ на синхронный REST-запрос: уведомление отправлено.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {

    private String status;
    private String message;
}
