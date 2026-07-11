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

## Сессия 6 — BCrypt вместо SHA-256

**Дата:** 2026-07-07

**Сделано:**
- `SecurityConfig.java` — создан (@Configuration, @Bean PasswordEncoder → BCryptPasswordEncoder)
- `UserServiceImpl.java` — `PasswordUtil.hashPassword()` → `passwordEncoder.encode()`, inject `PasswordEncoder`
- `PasswordUtil.java` — удалён из web-модуля
- `mvn clean compile -pl web -am` — common SUCCESS, web: 1 expected error (AuthenticationService ищет удалённый PasswordUtil)

## Сессия 7 — JWT Token Provider

**Дата:** 2026-07-07

**Сделано:**
- `web/pom.xml` — добавлены jjwt-api 0.12.6, jjwt-impl (runtime), jjwt-jackson (runtime)
- `application.yaml` — добавлены jwt.secret (base64 HMAC-SHA256) и jwt.expiration (24ч)
- `AuthResponse.java` — record: token, userId, username, role
- `JwtTokenProvider.java` — @Component с 3 методами: generateToken(), getUserIdFromToken(), validateToken()
- `mvn clean compile -pl web -am` — common SUCCESS, web: 1 expected error (AuthenticationService)

**Следующая сессия:** 9 — OAuth2 Client (GitHub Login)

## Сессия 8 — SecurityFilterChain + OAuth2 Resource Server

**Дата:** 2026-07-07

**Сделано:**
- `web/pom.xml` — добавлен spring-boot-starter-oauth2-resource-server
- `SecurityConfig.java` — переписан: @EnableMethodSecurity, SecurityFilterChain (whitelist /api/auth/**, /admin/** → ADMIN, rest authenticated), oauth2ResourceServer().jwt(), JwtDecoder (NimbusJwtDecoder через SecretKey от JwtTokenProvider), AuthenticationManager bean
- `JwtTokenProvider.java` — добавлен getSecretKey() геттер
- `application.yaml` — добавлен spring.security.oauth2.resourceserver.jwt (пустой jwk-set-uri)
- `AuthFilter.java`, `AdminFilter.java` — удалены из src/
- `mvn clean compile -pl web -am` — common SUCCESS, web: 28 errors (3 intentionally broken files: AuthenticationService, UserValidation, QuestionValidator)

**Intentionally broken (ждают будущих сессий):**
- `AuthenticationService.java` — 4 ошибки (Session 10)
- `UserValidation.java` — 14 ошибок (Session 16)
- `QuestionValidator.java` — 10 ошибок (Session 16)

## Сессия 9 — OAuth2 Client (GitHub Login)

**Дата:** 2026-07-07

**Сделано:**
- `web/pom.xml` — добавлен spring-boot-starter-oauth2-client
- `common/.../model/User.java` — добавлено поле githubId (@Column, unique)
- `common/.../repository/UserRepository.java` — добавлен findByGithubId
- `application.yaml` — добавлен spring.security.oauth2.client.registration.github (client-id/secret из переменных окружения, scope read:user + user:email)
- `config/oauth2/CustomOAuth2UserService.java` — создан (поиск/создание User по githubId или email, сохранение appUserId в атрибуты)
- `config/oauth2/OAuth2LoginSuccessHandler.java` — создан (генерация JWT, установка в HttpOnly cookie jwt, редирект на /)
- `config/SecurityConfig.java` — добавлен oauth2Login() с userService + successHandler, CookieBearerTokenResolver (читает jwt из cookie при отсутствии Authorization header), WebMvcConfigurer для /login → login.html, permit /login/** и /oauth2/**
- `templates/login.html` — создан (кнопка "Sign in with GitHub")
- `mvn clean compile -pl web -am` — common SUCCESS, web: 28 errors (3 intentionally broken файла)

## Сессия 10 — AuthController + Two-Token Pattern

**Дата:** 2026-07-07

**Сделано:**
- `LoginRequest.java` — record (String username, String password)
- `RegisterRequest.java` — record (String username, String password, String email)
- `InvalidCredentialsException.java` — extends ApiException, HTTP 401
- `GlobalExceptionHandler.java` — добавлен handleUnauthorized (InvalidCredentialsException → 401)
- `AuthenticationService.java` — переписан: BCrypt verify, InvalidCredentialsException, @RequiredArgsConstructor
- `AuthController.java` — @RestController:
  - POST /api/auth/login → 200 + access token (JSON) + refresh token (HttpOnly cookie)
  - POST /api/auth/register → 201 + access token (JSON) + refresh token (HttpOnly cookie)
  - POST /api/auth/refresh → 200 + новый access token + rotated refresh token (cookie)
- `RefreshToken.java` — entity (id, user, token UUID, expiresAt, revoked)
- `RefreshTokenRepository.java` — findByToken, findByUser, deleteByUser
- `RefreshTokenService.java` — createRefreshToken, validateAndRotate (revoke old → return User), revokeAllForUser
- `InvalidRefreshTokenException.java` — extends ApiException, HTTP 401
- `application.yaml` — jwt.expiration: 86400000 → 900000 (15min), + jwt.refresh-expiration: 2592000000 (30d)
- Удалены из src/: LoginServlet.java, RegistrationServlet, LogoutServlet.java
- `mvn clean compile -pl web -am` — common SUCCESS (13 files), web: 24 errors (2 intentionally broken файла)

**Intentionally broken (ждут будущих сессий):**
- `UserValidation.java` — 14 ошибок (Session 16)
- `QuestionValidator.java` — 10 ошибок (Session 16)

## Сессия 11 — ProfileController + Code Improvements

**Дата:** 2026-07-08

**Сделано — Profile REST API:**
- `ProfileResponse.java` — record (id, username, email, nickname, about, avatarPath, role, createdAt)
- `ProfileUpdateRequest.java` — record (nickname, about)
- `PasswordChangeRequest.java` — record (currentPassword, newPassword, confirmPassword)
- `UserMapper.java` — MapStruct interface (componentModel = spring)
- `UserService.java` — +updateProfile(), +changePassword(), +getProfile()
- `UserServiceImpl.java` — реализация updateProfile (set nickname/about), changePassword (BCrypt verify via PasswordEncoder)
- `ProfileController.java` — @RestController:
  - GET /api/profile → 200 + ProfileResponse
  - POST /api/profile → 200 + ProfileResponse (update nickname/about)
  - POST /api/profile/password → 200 (validate confirmPassword, BCrypt verify)
- Удалены из src/: ProfileServlet.java, ProfileEditServlet.java, AvatarSelectServlet.java

**Сделано — Code Improvements:**
- **#1 @Transactional + убрать save()** — @Transactional на write-методы, Hibernate Dirty Checking вместо явных save()
- **#2 UserRepository из контроллера** — вынесен в UserService.getProfile()
- **#3 CurrentUserService** — новый компонент для извлечения userId из JWT (DRY)
- **#5 Бизнес-проверки в сервис** — проверка confirmPassword перенесена из контроллера в UserServiceImpl.changePassword()
- **#6 Internal error → generic** — handleUnexpected() возвращает "Internal server error" вместо exception.getMessage()
- **#7 equals/hashCode на id** — User.equals() и hashCode() переведены с username на id (Hibernate best practice)
- `mvn clean compile -pl web -am` — common SUCCESS (13 files), web: 24 errors (2 intentionally broken файла)

**Intentionally broken (ждут будущих сессий):**
- `UserValidation.java` — 14 ошибок (Session 16)
- `QuestionValidator.java` — 10 ошибок (Session 16)

## Сессия 12 — TestController (опрос)

**Дата:** 2026-07-08

**Сделано — Test REST API:**
- `TestStartRequest.java` — record (List topics, int questionCount)
- `AnswerRequest.java` — record (int answerIndex)
- `AnswerItem.java` — record (int index, String text) — без correctAnswerIndex
- `QuestionResponse.java` — record (questionId, questionText, answers, questionNumber, totalQuestions, score, finished)
- `AnswerResultResponse.java` — record (correct, correctAnswerIndex, finished, score, totalQuestions, nextQuestion)
- `AnswerResult.java` — record (correct, correctAnswerIndex, finished, score, totalQuestions) — внутренний DTO
- `TestService.java` — @Service: startTest(TestStartRequest → InterviewState), processAnswer(InterviewState, int → AnswerResult)
- `TestController.java` — @RestController:
  - POST /api/test/start → 200 + QuestionResponse (первый вопрос)
  - GET /api/test/question → 200 + QuestionResponse (текущий вопрос) или 204 (нет теста)
  - POST /api/test/question → 200 + AnswerResultResponse (статус + следующий вопрос)

**Сделано — изменения:**
- `SecurityConfig.java` — sessionCreationPolicy: STATELESS → IF_REQUIRED (для HttpSession)
- `TopicLoader.java:21` — orElse(null) → orElseThrow(TopicNotFoundException) (фикс review #14)

**Удалено из src/:**
- `StartServlet.java` — полностью заменён TestController.startTest()
- `QuestionServlet.java` — полностью заменён TestController.getQuestion() + answerQuestion()

**Review-фиксы:**
- #6 QuestionServlet.java:71 — ✅ (файл удалён)
- #7 QuestionServlet.java:79 — ✅ (файл удалён)
- #14 StartServlet.java:46 — ✅ (файл удалён, логика в TopicLoader.findByCode с orElseThrow)
- #16 QuestionServlet.java:35 — ✅ (файл удалён)
- #21 TopicLoader.java:21 — ✅ (orElseThrow вместо null)

**Intentionally broken (ждут будущих сессий):**
- `UserValidation.java` — 14 ошибок (Session 16)
- `QuestionValidator.java` — 10 ошибок (Session 16)

## Session 13 — TestController (результат) | 2026-07-10

**Сделано — Test REST API:**
- `TestResultResponse.java` — record (correctAnswers, totalQuestions, passed, score) с компактным конструктором для процента
- `GET /api/test/result` — достаёт InterviewState из сессии, проверяет isExpired/isFinished, вызывает `saveResult()` + `processResult()`, очищает сессию

**Сделано — Доменная модель M:N:**
- `TestResult.java` — `@ManyToOne Topic` → `@ManyToMany Set<Topic>` с join table `test_results_topics` (review #4)
- `Topic.java` — `@OneToMany List<TestResult>` → `@ManyToMany(mappedBy="topics") Set<TestResult>`
- Конструктор TestResult: `Topic topic` → `Set<Topic> topics`
- `TestResultRepository.java` — EntityGraph: `"topic"` → `"topics"`

**Сделано — Split TestResultService:**
- `processAndSaveResult` разделён на `processResult(InterviewState)` (только вычисления) и `saveResult(User, InterviewState)` (только БД)
- Topics из `state.getTopics()` вместо `questions.get(0).getTopic()` (fix #4)

**Адаптированы под Set:**
- `AdminStatisticsService.java` — итерация по `r.getTopics()`
- `UserStatisticsServiceImpl.java` — итерация по `result.getTopics()`

**Удалено из src/:**
- `ResultServlet.java` — весь функционал перенесён в REST endpoint

**Review-фиксы:**
- #4 (ERROR) — ResultServlet questions.get(0) — @ManyToMany Set<Topic>
- #8 (WARNING) — ResultServlet:62 — файл удалён
- #13 (WARNING) — TestResultService:30 — split process + save

**Intentionally broken (ждут будущих сессий):**
- `UserValidation.java` — 14 ошибок (Session 16)
- `QuestionValidator.java` — 10 ошибок (Session 16)

## Session 14 — AdminControllers | 2026-07-11

**Сделано — Admin REST API (3 контроллера):**

**AdminUserController** (`/api/admin/users`):
- `GET /` — список пользователей
- `GET /{id}` — пользователь по ID
- `DELETE /{id}` — удалить пользователя
- `POST /{id}/block` — toggle block
- `PUT /{id}/role` — смена роли

**AdminTopicController** (`/api/admin/topics`):
- `GET /`, `POST /`, `DELETE /{code}` — CRUD тем
- `GET /{code}/questions`, `POST /{code}/questions`, `PUT /{code}/questions/{id}`, `DELETE /{code}/questions/{id}` — CRUD вопросов
- `POST /{code}/import` — JSON import (MultipartFile)

**AdminStatisticsController** (`/api/admin/statistics`):
- `GET /` — статистика (totalTests, passedTests, userStats, topicStats)

**Сделано — изменения:**
- `AdminUserService.java` — @Transactional, новые методы (getAllUsers, getUserById, deleteUser, toggleBlockUser), cleanup save() → dirty checking
- `SecurityConfig.java` — добавлен `.requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())`
- `AdminTestService.java` — убран мёртвый null-check в deleteTopic()

**DTO (4 новых):**
- `RoleUpdateRequest`, `TopicRequest`, `QuestionCreateRequest`, `QuestionUpdateRequest`

**Удалено из src/:**
- `AdminServlet.java`, `AdminUserServlet.java`, `AdminBlockUserServlet.java`, `AdminChangeRoleServlet.java`, `AdminTestServlet.java`, `AdminStatisticsServlet.java`

**Review-фиксы:**
- #6 (ERROR) — AdminBlockUserServlet:27 → ✅ (сервлет удалён)
- #7 (ERROR) — AdminChangeRoleServlet:31 → ✅ (сервлет удалён)
- #8 (ERROR) — AdminUserService:40 → ✅ (@Transactional + dirty checking)
- #10 (WARNING) — AdminUserServlet:82 → ✅ (файл удалён)
- #11 (WARNING) — AdminTestServlet:262 → ✅ (файл удалён)
- #17 (WARNING) — AdminUserServlet:56 → ✅ (файл удалён)
- #20 (WARNING) — AdminUserServlet:82 dup → ✅ (файл удалён)

**Intentionally broken (ждут будущих сессий):**
- `UserValidation.java` — 14 ошибок (Session 16)
- `QuestionValidator.java` — 10 ошибок (Session 16)

**Следующая сессия:** 15 — DTO + Mappers
