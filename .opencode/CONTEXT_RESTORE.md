# Промт для восстановления контекста — Session 26

> Скопируй это сообщение в начало нового диалога с opencode для восстановления контекста JavaTraining.

---

Этот промт для восстановления контекста. Пока ничего делать не нужно.

**Objective:** Продолжить разработку JavaTraining (Spring Boot 3.3.5, PostgreSQL 16). Текущий этап — Liquibase внедрён, дальше Session 27 — Redis.

**Important Details:**
- Приложение на `http://localhost:8080`, БД — `docker compose up -d postgres minio`
- Схема БД управляется Liquibase (не `ddl-auto: update`), changelog в `common/src/main/resources/db/changelog/`
- При старте приложения Liquibase автоматически накатывает миграции
- volumes data: `./docker/volumes/postgres/`, `./docker/volumes/minio/`
- `spring.jpa.hibernate.ddl-auto: validate` — проверяет схему на старте
- Первый зарегистрированный пользователь получает ADMIN
- OAuth2 GitHub требует `GITHUB_CLIENT_ID` / `GITHUB_CLIENT_SECRET`
- Multipart: max 10MB, валидация 2MB в AvatarService

**Build:** `mvn compile -q` (без тестов)
**Start command:** `mvn spring-boot:run -pl web` из корня проекта

**Last commit:** (не закоммичено — unpushed изменения Session 26)

**Session 26 completed (2026-07-16):**
- POM: liquibase-maven-plugin (parent) + liquibase-core (common)
- Changelog: `db.changelog-master.yaml` + `v001-init-schema.yaml` (8 changesets: users, topics, questions, answers, test_results, test_results_topics, refresh_tokens)
- `ddl-auto: validate` вместо `update`
- docker-compose: init.sql удалён, paths → `./docker/volumes/`
- `javatraining/` → `docker/volumes/` (bind mount data)

**Known issues for next session (Session 27 — Redis):**
1. InterviewState в HttpSession — перенести в Redis (`@EnableRedisHttpSession`)
2. Добавить Redis в docker-compose (порт 6379)
3. Добавить spring-session-data-redis + lettuce-core в POM
4. Настроить application.yaml для Redis

**Relevant files:**
- `.opencode/AGENTS.md` — полный контекст проекта
- `.opencode/PLAN.md` — план сессий 25a–37
- `.opencode/CHANGELOG.md` — лог всех изменений по сессиям
- `common/src/main/resources/db/changelog/` — Liquibase миграции
- `docker/volumes/` — данные Docker контейнеров (БД + MinIO)
