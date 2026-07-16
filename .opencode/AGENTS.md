# AGENTS.md — Контекст проекта JavaTraining

## 1. О проекте

Учебное веб-приложение для подготовки к собеседованиям по Java. Пользователи проходят тесты по темам (Java Core, Concurrency и т.д.), получают результаты, администраторы управляют контентом.

**Текущее состояние:** Spring Boot монолит с Thymeleaf, REST API + JWT  
**Цель:** Микросервисы через Kafka (после Liquibase, Redis, React фронтенда)

## 2. Технологический стек (целевой)

| Категория | Технология |
|-----------|-----------|
| **Язык** | Java 21 |
| **Фреймворк** | Spring Boot 3.3.x |
| **ORM** | Spring Data JPA (Hibernate 6) |
| **БД** | PostgreSQL 16 (prod), H2/TestContainers (test) |
| **Security** | Spring Security, JWT, OAuth2 Resource Server, OAuth2 Client (GitHub), BCrypt |
| **Web** | Thymeleaf → React (фаза 13) |
| **DTO** | MapStruct 1.6.x |
| **Lombok** | 1.18.34 |
| **Kafka** | Spring Kafka (фаза микросервисов) |
| **Хранилище** | MinIO (аватары) |
| **Тесты** | JUnit 5, Mockito, AssertJ, TestContainers, JaCoCo |
| **Мониторинг** | Spring Actuator, Micrometer, Prometheus (опционально) |

## 3. Доменная модель

```
User (id, username, passwordHash, email, nickname, about, avatarPath, role, createdAt, blocked, version)
  └── 1:N → TestResult (id, user, topics M:N, totalQuestions, correctAnswers, passed, finishedAt, version)

Topic (id, code, displayName, version)
  ├── 1:N → Question (id, questionText, topic, correctAnswerIndex, answers, version)
  │             └── 1:N → Answer (id, answerText, answerIndex, question, version)
  └── M:N → TestResult (through test_results_topics join table)

InterviewState (session-only, НЕ entity)
  - topics: Set<Topic>
  - questions: List<Question>
  - currentIndex: int
  - score: int
  - createdAt: LocalDateTime
```

## 4. Роли

| Роль | Обязанности |
|------|------------|
| **Вы (пользователь)** | Пишете код, принимаете решения, коммитите |
| **opencode (я)** | Объясняю что/зачем/почему, даю план и инструкции, проверяю |

## 5. Глоссарий

| Термин | Значение |
|--------|---------|
| **Monolith First** | Сначала полный Spring Boot монолит, затем выделение микросервисов |
| **Session (сессия общения)** | Один сеанс работы с opencode. Завершается коммитом и обновлением AGENTS.md |
| **Этап** | Логическая группа сессий (например, "Core Migration", "Security") |
| **InterviewState** | Состояние активного теста: текущий вопрос, количество правильных ответов |
| **Topic** | Тема теста (например, "java-core", "java-concurrency") |
| **Review issue** | Замечание из code review JSON, которое нужно исправить |

## 6. Правила

- Коммиты делает пользователь самостоятельно. Я не выполняю git commit/push.
- После каждой сессии обновляется AGENTS.md (раздел "Прогресс")
- В начале сессии — чтение AGENTS.md + PLAN.md для восстановления контекста
- **Пишешь код самостоятельно ТОЛЬКО для:** файлов `.opencode/*` и фронтенда (React, TypeScript, CSS, JS, HTML). Всю Java-кодовую базу пользователь пишет сам по моим инструкциям. Исключение — если ты явно попросишь меня написать что-то.
- **План перед изменениями:** перед любым действием (создание, редактирование, удаление файлов) я обязан написать план изменений с объяснением: *где* именно (файл + строка/секция), *зачем* это нужно (какую проблему решает), *почему* выбран именно такой подход, и *какую пользу* это приносит проекту (упрощение, безопасность, производительность, подготовка к следующему шагу). После плана мы определяем, что делаешь ты (Java-код), а что делаю я (удаление файлов, .opencode/*, фронтенд).
- **Объяснение изменений:** каждое изменение должно сопровождаться пояснением: *зачем* оно нужно (какую проблему решает), *где* именно сделано (файл + строка/секция), *почему* выбран именно такой подход, и *какую пользу* это приносит проекту (упрощение, безопасность, производительность, подготовка к следующему шагу).
- **Завершение сессии:** когда ты говоришь "заканчиваю сессию" (или аналогично), я обновляю AGENTS.md (прогресс), CHANGELOG.md, PLAN.md (статус задач) и пишу промт для быстрого восстановления контекста на следующую сессию.

## 7. Список замечаний ревью (30 шт)

Группировка по severity:
- **ERROR (12):** PasswordUtil SHA-256, ProfileEditServlet хэш, AdminChangeRoleServlet valueOf, AvatarUploadServlet substring, AdminBlockUserServlet репозиторий, AdminUserService права, AdminStatisticsService отсутствие, AdminStatisticsService прямые запросы, остальные ERROR
- **WARNING (18):** SRP, дублирование, локализация, мёртвый код, лямбды в контроллерах и т.д.
- **INFO (7):** константы, комментарии, магические числа

Решённые в Session 13: #4 (ERROR — @ManyToMany), #8 (WARNING — ResultServlet удалён), #13 (WARNING — split process+save)
Решённые в Session 14: #6 (ERROR AdminBlockUserServlet), #7 (ERROR AdminChangeRoleServlet valueOf), #8 (ERROR AdminUserService save), #10 (WARNING AdminUserServlet:82), #11 (WARNING AdminTestServlet:262), #17 (WARNING AdminUserServlet:56), #20 (WARNING AdminUserServlet:82 dup)
Полный список → PLAN.md (чек-лист ревью)

## 8. Прогресс

_Обновляется в конце каждой сессии_

| Сессия | Статус | Дата |
|--------|--------|------|
| 0 | ✅ Выполнена | 2026-07-04 |
| 1 | ✅ Выполнена | 2026-07-05 |
| 2 | ✅ Выполнена | 2026-07-05 |
| 3 | ✅ Выполнена | 2026-07-05 |
| 4 | ✅ Выполнена | 2026-07-05 |
| 5 | ✅ Выполнена | 2026-07-07 |
| 6 | ✅ Выполнена | 2026-07-07 |
| 7 | ✅ Выполнена | 2026-07-07 |
| 8 | ✅ Выполнена | 2026-07-07 |
| 9 | ✅ Выполнена | 2026-07-07 |
| 10 | ✅ Выполнена | 2026-07-07 |
| 11 | ✅ Выполнена | 2026-07-08 |
| 12 | ✅ Выполнена | 2026-07-08 |
| 13 | ✅ Выполнена | 2026-07-10 |
| 14 | ✅ Выполнена | 2026-07-11 |
| 15 | ✅ Выполнена | 2026-07-11 |
| 16 | ✅ Выполнена | 2026-07-11 |
| 17 | ✅ Выполнена | 2026-07-11 |
| 18 | ✅ Выполнена | 2026-07-11 |
| 19 | ✅ Выполнена | 2026-07-11 |
| 20 | ✅ Выполнена | 2026-07-12 |
| 21 | ✅ Выполнена | 2026-07-12 |
| 22 | ✅ Выполнена | 2026-07-14 |
| 23 | ✅ Выполнена | 2026-07-14 |
| 24 | ✅ Выполнена | 2026-07-14 |
| 25a | ✅ Выполнена | 2026-07-16 |
| 26 | ✅ Выполнена | 2026-07-16 |
| 27 | 🔲 Redis | — |
| 28 | 🔲 Удаление Thymeleaf | — |
| 29 | 🔲 React Auth + Profile | — |
| 30 | 🔲 React Tests + Admin | — |
| 31 | 🔲 Kafka Event Bus | — |
| 32 | 🔲 Eureka + Gateway | — |
| 33 | 🔲 auth-service | — |
| 34 | 🔲 user-service | — |
| 35 | 🔲 test-service | — |
| 36 | 🔲 admin-service + интеграция | — |
| 37 | 🔲 Final Review | — |
