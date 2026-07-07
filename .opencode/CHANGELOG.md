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

## Сессия 2 — application.yml + Spring Boot config

**Дата:** 2026-07-05

**Сделано:**
- `application.yaml` (datasource, jpa, hikari, logging) — написан пользователем
- `logback-spring.xml` — создан (console + rolling file, уровни DEBUG/TRACE)
- `WebApplication.java` — перенесён в пакет `com.homeapp.javatraining`
- Удалены: `hibernate.cfg.xml`, `HibernateUtil.java`, `CommonApplication.java`, `log4j2.xml`
- Удалены: `application.properties` (common + web), заменены `.yaml`
- `mvn clean compile -pl web -am` — BUILD SUCCESS

**Следующая сессия:** 3 — Spring Data JPA репозитории

## Сессия 3 — Spring Data JPA репозитории

**Дата:** 2026-07-05

**Сделано:**
- Entity перенесены в `common` (Answer, Question, Topic, User, TestResult, Role, InterviewState)
- `org.hibernate.annotations.Index` → `jakarta.persistence.Index` в `@Table` (Hibernate 6 compat)
- `UserRepository extends JpaRepository` — `findByUsername`, `findFirstBy`
- `QuestionRepository extends JpaRepository` — `findByTopic`, `findById`, `findAll` с `@EntityGraph`
- `TopicRepository extends JpaRepository` — `findByCode`
- `TestResultRepository extends JpaRepository` — `findByUserId`, `findAll` с `@EntityGraph`
- Удалены 8 старых файлов репозиториев из `src/` (4 интерфейса + 4 Hibernate\*Repository)
- `mvn clean compile -pl web -am` — BUILD SUCCESS

**Следующая сессия:** 4 — @Service + DI

## Сессия 4 — @Service + DI

**Дата:** 2026-07-05

**Сделано:**
- 10 сервисов перенесены `src/` → `web/.../service/`
- Добавлены `@Service` всем сервисам, `@RequiredArgsConstructor` (6 explicit constructors → Lombok)
- `AuthenticationService`, `RegistrationService`, `QuestionService` — `@Service`, explicit constructors оставлены (factory-валидаторы)
- `TopicLoader` — `@Component` добавлен
- Перенесены в `web/`: `PasswordUtil`, DTO (4), Exception (2), `ValidationFactory`, `QuestionValidator`, `UserValidation`
- Исправлены вызовы репозиториев: `findByUserName`→`findByUsername`, `findAny`→`findFirstBy`, `getQuestions`→`findByTopic`
- Удалены: `ApplicationConfig.java`, `AppInitListener.java`, `BaseServlet.java`
- `ValidationFactory.createErrorHandler()` удалён (не использовался)
- `mvn clean compile -pl web -am` — BUILD SUCCESS (24 source files in web)

**Review-фиксы:** #10 (AdminStatisticsService), #11 (BaseServlet), #12 (AuthenticationService)

## Сессия 5 — Global Exception Handler

**Дата:** 2026-07-07

**Сделано:**
- `ErrorResponse` record (int status, String error, String message, String field) — создан
- `ApiException` abstract base class — создан
- Пакет `exception/user/`: `UserNotFoundException`, `DuplicateUsernameException`, `DuplicateEmailException`, `CannotChangeOwnRoleException` — созданы
- Пакет `exception/topic/`: `TopicNotFoundException` — создан
- Пакет `exception/question/`: `QuestionNotFoundException`, `NotEnoughQuestionsException`, `QuestionImportException` — созданы
- `ValidationException` упрощён (extends ApiException, статические фабрики удалены)
- `AuthenticationException` удалён из web-модуля
- `GlobalExceptionHandler` с `@RestControllerAdvice` (6 handler-методов) — создан
- Сервисы обновлены: `UserServiceImpl`, `AdminUserService`, `QuestionService`, `AdminTestService` — переведены на кастомные исключения
- Удалены (6 файлов): `ErrorHandler.java`, `RequestHandler.java`, `AuthenticationException.java` (старый), `ValidationException.java` (старый), `ErrorHandlerTest.java`, `RequestHandlerTest.java`
- `mvn clean compile -pl web -am` — common SUCCESS, web: 27 expected errors (3 intentionally broken files)

**Intentionally broken (ждут Spring Security / Bean Validation):**
- `AuthenticationService.java` — 3 ошибки (будет переписан в Session 6)
- `UserValidation.java` — 14 ошибок (будет переписан в Session 15)
- `QuestionValidator.java` — 10 ошибок (будет переписан в Session 15)

**Следующая сессия:** 6 — BCrypt вместо SHA-256
