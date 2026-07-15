# JavaTraining

Учебное веб-приложение для подготовки к собеседованиям по Java.

## Технологический стек

| Категория | Технология |
|-----------|-----------|
| **Язык** | Java 21 |
| **Фреймворк** | Spring Boot 3.3.x |
| **ORM** | Spring Data JPA (Hibernate 6) |
| **БД** | PostgreSQL 16 |
| **Security** | Spring Security, JWT (OAuth2 Resource Server), OAuth2 Client (GitHub), BCrypt |
| **Web** | Thymeleaf → React (фаза 13) |
| **DTO** | MapStruct 1.6.x |
| **Lombok** | 1.18.34 |
| **Хранилище** | MinIO (аватары) |
| **Тесты** | JUnit 5, Mockito, AssertJ |
| **Сборка** | Maven (модули: common, web) |

## Архитектура

Монолит (Spring Boot) → миграция к микросервисам через Kafka.

**Модули:**
- `common` — JPA entities, DTO, репозитории
- `web` — REST API, сервисы, безопасность, Thymeleaf-шаблоны (будет заменён на React)

## Быстрый старт

```bash
# Запуск инфраструктуры (PostgreSQL, MinIO, Kafka, ZooKeeper)
docker-compose up -d

# Сборка
mvn clean install -DskipTests

# Запуск
mvn spring-boot:run -pl web
```

## REST API

| Эндпоинт | Описание |
|----------|----------|
| `POST /api/auth/login` | Вход (JWT + refresh token) |
| `POST /api/auth/register` | Регистрация |
| `POST /api/auth/refresh` | Обновление токенов |
| `GET /api/profile` | Профиль пользователя |
| `POST /api/profile` | Обновление профиля |
| `POST /api/profile/password` | Смена пароля |
| `POST /api/test/start` | Начать тест |
| `GET /api/test/question` | Текущий вопрос |
| `POST /api/test/question` | Ответ на вопрос |
| `GET /api/test/result` | Результат теста |
| `GET/POST/DELETE /api/admin/users` | Управление пользователями |
| `GET/POST/DELETE /api/admin/topics` | Управление темами |
| `GET/POST/PUT/DELETE /api/admin/topics/{code}/questions` | Управление вопросами |
| `GET /api/admin/statistics` | Статистика |

## Структура проекта

```
common/
  src/main/java/com/homeapp/javatraining/
    model/          # JPA entities
    repository/     # Spring Data JPA репозитории
    dto/            # DTO records
    mapper/         # MapStruct мапперы
    exception/      # Исключения
  src/main/resources/questions/   # JSON с вопросами
web/
  src/main/java/com/homeapp/javatraining/
    config/         # Spring Config (security, minio)
    controller/     # REST контроллеры
    controller/admin/  # Admin REST контроллеры
    service/        # Бизнес-логика
    oauth2/         # OAuth2 клиент (GitHub)
  src/main/resources/
    templates/      # Thymeleaf (будет заменён на React)
    static/         # CSS, JS
docker-compose.yml  # PostgreSQL, MinIO, Kafka, ZooKeeper
```

## Roadmap

Сессии 25a–37 в `.opencode/PLAN.md`:
- 25a: Очистка проекта
- 26: Liquibase
- 27: Redis (session state)
- 28: Удаление Thymeleaf
- 29–30: React фронтенд
- 31: Kafka Event Bus
- 32: Eureka + Gateway
- 33–36: Микросервисы (auth, user, test, admin)
- 37: Final Review
