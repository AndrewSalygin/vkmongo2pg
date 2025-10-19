# VK Mongo to Postgres Converter

Сервис для конвертации постов `ВКонтакте` из `MongoDB` в `PostgreSQL`.

## Описание

Этот проект работает **в паре с проектом 
[VK Wall Parser](https://github.com/AndrewSalygin/vkwallparser)**, который подключается к 
`API ВКонтакте` и сохраняет посты в `MongoDB`.

`vkmongo2pg` читает эти данные из `MongoDB`, **отбирает только необходимые поля, конвертирует их** 
и сохраняет в базу `PostgreSQL`.

Используется:
- `Java 21` + `Spring Framework` (без `Spring Boot`)
- `Spring Data MongoDB` (`MongoTemplate`) для чтения данных из `MongoDB`
- `JdbcTemplate` для сохранения в `PostgreSQL`
- `Docker Compose` для локального запуска `MongoDB` и `PostgreSQL`
- `.env` файл для хранения конфиденциальных данных

### Подготовка и запуск

1. Убедитесь, что проект **[VK Wall Parser](https://github.com/AndrewSalygin/vkwallparser)** скачал данные в MongoDB.
2. Создайте `.env` файл с секретами по примеру `.env.example`, указав параметры для MongoDB и PostgreSQL.
3. Запустите базы данных через Docker Compose:
   ```bash
   docker compose up -d
   ```
4. Скомпилируйте проект:
    ```bash
    mvn clean compile
    ```
5. Запустите сервис миграции:
    ```bash
   mvn exec:java
    ```