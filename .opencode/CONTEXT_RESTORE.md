# Промт для восстановления контекста — Session 23

> Скопируй это сообщение в начало нового диалога с opencode для восстановления контекста JavaTraining.

---

Этот промт для восстановления контекста. Пока ничего делать не нужно.

**Objective:** Продолжить ручное клиентское тестирование JavaTraining (Spring Boot 3.3.5, Thymeleaf + REST API + JWT, PostgreSQL 16)

**Important Details:**
- Приложение на `http://localhost:8080`, БД — `docker compose up -d postgres`
- Первый зарегистрированный пользователь получает ADMIN
- 9 тем и 669 вопросов уже в БД (импортированы из JSON)
- Schema fix: `test_results` НЕ содержит колонку `topic_id`, связь M:N через `test_results_topics`
- Refresh token НЕ создаётся при формовой регистрации/входе (только REST API) — так и задумано
- OAuth2 GitHub требует `GITHUB_CLIENT_ID` / `GITHUB_CLIENT_SECRET`
- Multipart: max 10MB, валидация 2MB в AvatarService

**Build:** `mvn compile -q` (без тестов)

**Start command:** `mvn spring-boot:run` из корня проекта

**Last commit:** `1c790b9 Frontend QA: Auth Fixes + User Badge`

**Work State:**

Completed Session 22 (2026-07-14):
- TEST_PLAN.md полностью переработан: Postman-инструкции, новые фазы (my-stats, JSON import, edit question, avatar upload, admin statistics, OAuth2), обновлены предусловия
- FormLoginFailureHandler — отдельное сообщение для заблокированных пользователей (/login?blocked)
- Profile nickname: всегда виден, username как fallback
- Upload >2MB: адекватная ошибка вместо connection reset
- Empty topics validation в TestService.startTest()
- /test/result → /test/settings (если тест не завершён)
- GlobalExceptionHandler → только @RestController (+ MvcExceptionHandler для @Controller)
- AdminUserService: Long.equals() вместо == (смена роли себе не блокировалась)
- JWT: HS512 явно (алгоритм mismatch — 401 Unauthorized при валидном токене)
- Per-topic stats: комбинированные тесты исключены
- Test settings: CSS grid для чекбоксов
- Admin dashboard: убрана стат-карточка "Тем"
- Уведомления о комбинированных тестах (JS на /test/settings + заметка на /my-stats)
- "Счёт: X" → "Правильных: X из Y" на странице вопроса

Active: (none)

Blocked: (none)

Known issues for next session:
1. Нет unit-тестов — нужно начать с сервисных слоёв (JUnit 5 + Mockito)
2. Нет пагинации в админ-таблицах
3. Refresh token cookie path `/api/auth/refresh` — не подходит для SPA
4. Нет preview загруженного аватара перед сохранением
5. Нет REST-эндпоинта для смены аватара
6. Аватар при OAuth2 — не импортируется из GitHub, ставится дефолтный #1

Relevant files:
- `.opencode/AGENTS.md` — полный контекст проекта
- `.opencode/PLAN.md` — план сессий и чек-лист ревью
- `.opencode/CHANGELOG.md` — лог всех изменений по сессиям
- `TEST_PLAN.md` — актуальный план ручного тестирования
