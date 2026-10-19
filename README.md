# kafka-workshop-notification-service

Учебный проект с воркшопа **Coding GYM / Java — «Асинхронщина: Kafka — трогаем руками»** (Kata Academy).

Работает в паре с [kafka-workshop-credit-service](../kafka-workshop-credit-service) — начните с его README: там `docker-compose.yml` с Kafka и полный гайд.

## О проекте

**notification-service** получает решение по кредиту и отправляет уведомление в Telegram-канал. Решение приходит двумя путями:

| Канал | Откуда | Как |
|---|---|---|
| REST | `POST /api/notifications` | credit-service ждёт ответа |
| Kafka | топик `kafka.workflow.topic` | сервис сам читает сообщения (`@KafkaListener`) |

Сервис специально «думает» 3 секунды над каждым уведомлением (`NOTIFICATION_DELAY_MS`), чтобы была видна разница между синхронным и асинхронным вызовом.

notification-service — **consumer**, группа `kafka-workshop-notification-service-consumer-group`, `auto-offset-reset: earliest`. При ошибке обработки сообщение повторяется (`DefaultErrorHandler` + `FixedBackOff`: 500 мс × 5).

**Стек:** Java 25, Spring Boot 4, Spring Kafka, Telegram Bot API, Lombok.

## Структура

```
config/      RestTemplateConfig — клиент к api.telegram.org
controller/  NotificationApiController — REST-вход
dto/         CreditDecisionDto, NotificationResponseDto
kafka/       KafkaConsumerConfig, CreditDecisionListener, CreditDecisionHandler, KafkaService
service/     NotificationService (формирует текст), TelegramService (отправляет в канал)
```

## Как запустить

1. Поднимите Kafka из credit-service: `cp .env.sample .env && docker compose up -d`.
2. Запустите credit-service (8080), затем notification-service (8081).
3. Шлите запросы в credit-service из Postman — уведомления придут в канал, а в логах listener'а видно topic, partition, offset и key.

Без токена бота сервис не падает — просто пишет уведомление в лог.

## Свой Telegram-бот

1. В Telegram откройте [@BotFather](https://t.me/BotFather) → `/newbot` → получите токен.
2. Создайте канал и добавьте бота в администраторы с правом публикации.
3. Задайте переменные окружения (в IDEA: `Run → Edit Configurations → Environment variables`):

```
TELEGRAM_BOT_TOKEN=<токен от BotFather>
TELEGRAM_CHAT_ID=@имя_вашего_канала
```

> Токен — это пароль от бота. Не коммитьте его в git и не вставляйте в `application.yaml`: только переменные окружения.

## Что попробовать

- Выключите сервис, отправьте несколько `/async`-заявок, включите — сообщения дочитаются с последнего offset.
- Запустите 2–4 экземпляра (разные `SERVER_PORT`) — у топика 3 партиции, кто-то будет простаивать.
- Смените `group-id` — новая группа прочитает топик с начала.
- Добавьте Dead Letter Topic для сообщений, которые не удалось обработать.
- Сохраняйте уведомления в БД, добавьте ещё один канал (email).

## Переменные окружения

| Переменная | По умолчанию |
|---|---|
| `KAFKA_BOOTSTRAP_SERVER` | `localhost:9092,localhost:9094,localhost:9096` |
| `SERVER_PORT` | `8081` |
| `KAFKA_TOPIC_CREDIT` | `kafka.workflow.topic` |
| `NOTIFICATION_DELAY_MS` | `3000` |
| `TELEGRAM_BOT_TOKEN` | — |
| `TELEGRAM_CHAT_ID` | — |
