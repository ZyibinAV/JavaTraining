# CHANGELOG

## Сессия 0 — Подготовка контекста

**Дата:** 2026-07-04

**Сделано:**
- Создан `AGENTS.md` — архитектура, роли, глоссарий, стек, правила
- Создан `PLAN.md` — план миграции на 24 сессии, чек-лист ревью
- Создан `CHANGELOG.md` — лог изменений по сессиям
- Утверждён формат работы (Monolith First, 24 сессии, коммиты session-N)
- Утверждены правила: я пишу только `.opencode/*` и фронтенд, ты — Java-код

## Сессия 1 — POM + модули

**Дата:** 2026-07-05

**Сделано:**
- Parent POM: `packaging pom`, модули `common` и `web`
- Добавлены starters: web, data-jpa, security, thymeleaf, validation, actuator
- MapStruct 1.6.3 + Lombok annotation processors
- Удалены: jakarta-servlet-api, jstl, hibernate-core, log4j2, jackson-databind, h2, HikariCP
- `common/pom.xml` — jar, data-jpa, validation, mapstruct, postgresql
- `web/pom.xml` — war, зависит от common, web+security+thymeleaf+actuator, spring-boot-maven-plugin
- `docker-compose.yml` — добавлены MinIO, ZooKeeper, Kafka (заготовки)
- `mvn clean compile` — BUILD SUCCESS (3 модуля)

**Следующая сессия:** 2 — application.yml + Spring Boot config
