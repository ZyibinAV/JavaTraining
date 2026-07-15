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

## Session 15 — DTO + MapStruct | 2026-07-11

**Сделано — DTO (5 новых records):**
- `UserDTO.java` — id, username, email, nickname, about, avatarPath, role, createdAt, blocked
- `TopicDTO.java` — code, displayName
- `AnswerDTO.java` — index, text (замена AnswerItem)
- `QuestionDTO.java` — id, questionText, correctAnswerIndex, topicCode, answers
- `TestResultDTO.java` — id, userId, topicCodes, totalQuestions, correctAnswers, passed, finishedAt

**Сделано — Mappers (4 новых + 1 дополнен):**
- `AnswerMapper.java` — toAnswerDTO, toAnswerDTOList (mapping answerIndex→index, answerText→text)
- `TopicMapper.java` — toTopicDTO, toTopicDTOList
- `QuestionMapper.java` — toQuestionDTO (uses AnswerMapper, topic.code→topicCode)
- `TestResultMapper.java` — toTestResultDTO (user.id→userId, Set<Topic>→Set<String> via @Named)
- `UserMapper.java` — добавлен toUserDTO(User)

**Сделано — Контроллеры:**
- `AdminUserController.java` — getAllUsers/getUserById возвращают UserDTO вместо User (через UserMapper)
- `AdminTopicController.java` — getAllTopics → List<TopicDTO>, getQuestions → List<QuestionDTO>; удалены toQuestionResponse/toAnswerItems
- `QuestionResponse.java` — List&lt;AnswerItem&gt; → List&lt;AnswerDTO&gt;
- `TestController.java` — toAnswerItems создаёт AnswerDTO вместо AnswerItem

**Удалено:**
- `AnswerItem.java` — заменён на AnswerDTO (поля идентичны: index, text)

**Review-фиксы:** нет (решались архитектурные issues из review не затрагивались)

**Build:** 24 expected errors (14 UserValidation + 10 QuestionValidator)

## Session 16 — Bean Validation | 2026-07-11

**Сделано — Jakarta Validation на request DTO (9 файлов):**
| DTO | Аннотации |
|-----|-----------|
| `RegisterRequest` | `@NotBlank @Size(min=3,max=20) @Pattern` username; `@NotBlank @Size(min=6,max=100)` password; `@NotBlank @Email @Size(max=100)` email |
| `LoginRequest` | `@NotBlank` username, password |
| `ProfileUpdateRequest` | `@Size(max=50)` nickname; `@Size(max=500)` about |
| `PasswordChangeRequest` | `@NotBlank` currentPassword, confirmPassword; `@NotBlank @Size(min=6,max=100)` newPassword |
| `TestStartRequest` | `@NotEmpty` topics; `@Min(1)` questionCount |
| `AnswerRequest` | `@Min(0)` answerIndex |
| `RoleUpdateRequest` | `@NotBlank` role |
| `TopicRequest` | `@NotBlank @Size(max=50)` code; `@NotBlank @Size(max=100)` displayName |
| `QuestionCreateRequest` + `QuestionUpdateRequest` | `@NotBlank` questionText; `@Min(0)` correctAnswerIndex; `@NotEmpty @Size(min=2)` answers |

**Сделано — @Valid на контроллеры (5 файлов):**
- AuthController: login/register
- ProfileController: updateProfile/changePassword
- TestController: startTest/answerQuestion
- AdminUserController: changeRole
- AdminTopicController: createTopic/createQuestion/updateQuestion

**Сделано — GlobalExceptionHandler:**
- `handleMethodArgumentNotValid` — перехват `MethodArgumentNotValidException` → `ErrorResponse` (400 + field + message)

**Сделано — MessageSource:**
- `application.yaml`: `spring.messages.basename=messages`
- `messages.properties`: русские/английские сообщения для constraint violations

**Удалено (3 файла):**
- `UserValidation.java` — 14 ошибок, заменён DTO-аннотациями
- `QuestionValidator.java` — 10 ошибок, заменён inline-валидацией в AdminTestService
- `ValidationFactory.java` — больше не нужен

**Обновлены сервисы:**
- `RegistrationService.java` — удалён UserValidation + ValidationFactory
- `QuestionService.java` — удалён QuestionValidator, inline answerIndex validation
- `AdminTestService.java` — QuestionValidator.validate → inline ValidationException

**Build:** BUILD SUCCESS, 0 errors ✅

**Следующая сессия:** 17 — Thymeleaf Frontend

## Session 17 — Layout + Auth templates | 2026-07-11

**Сделано — Зависимости:**
- `web/pom.xml` — добавлены `thymeleaf-layout-dialect`, `thymeleaf-extras-springsecurity6`

**Сделано — Security (Java, пользователь):**
- `config/CustomUserDetailsService.java` — UserDetailsService для form login (загрузка User из БД, authorities по роли)
- `config/FormLoginSuccessHandler.java` — AuthenticationSuccessHandler: генерация JWT + HttpOnly cookie `jwt` + редирект на `/`
- `config/SecurityConfig.java` — formLogin() с кастомной страницей `/login`, logout (чистка cookie `jwt`), permitAll для `/register/**`, `/css/**`

**Сделано — Frontend:**
- `templates/layout.html` — Layout Dialect decorator с навигацией (авторизован/нет, ADMIN)
- `templates/login.html` — расширен: форма username+password, GitHub OAuth2, error/logout сообщения, использует layout
- `templates/register.html` — форма регистрации (username, email, password) с валидацией HTML5
- `static/css/style.css` — базовая стилизация (CSS vars, navbar, cards, формы, кнопки, таблицы, адаптивность)

**Сделано — ViewController (Java, пользователь):**
- `controller/ViewController.java` — @Controller:
  - GET /login, GET /register → возвращают template
  - POST /register → RegistrationService.registerUser() + JWT cookie + redirect
- `config/SecurityConfig.java` — дублирование `/login` убрано (WebMvcConfigurer → ViewController)

**Build:** `mvn clean compile -pl web -am` — BUILD SUCCESS ✅

## Session 18 — Main templates (home, profile, test pages) | 2026-07-11

**Сделано — Frontend (opencode):**
- `static/css/style.css` — +150 строк CSS (профиль, аватары, прогресс-бар, тест, результат)
- `templates/home.html` — главная с приветствием и карточками-ссылками
- `templates/profile.html` — отображение профиля (аватар, поля, роль, дата)
- `templates/profile-edit.html` — форма nickname + about
- `templates/avatar-select.html` — сетка 12 аватаров
- `templates/test-settings.html` — чекбоксы тем + выбор кол-ва вопросов
- `templates/question.html` — прогресс-бар, текст вопроса, radio-ответы
- `templates/result.html` — счёт, процент, badge passed/failed

**Сделано — Java (пользователь):**
- `controller/ViewController.java` — +6 методов для `/`, `/profile`, `/profile/edit`, `/profile/avatar`
- `controller/TestViewController.java` — новый @Controller с 5 методами для test flow

**Build:** `mvn clean compile -pl web -am` — BUILD SUCCESS ✅ (75 source files)

## Session 19 — Admin templates | 2026-07-11

**Сделано — Java (opencode):**
- `controller/admin/AdminViewController.java` — новый @Controller с 10 методами:
  - GET /admin — дашборд
  - GET/POST /admin/users, /admin/users/{id}/block, /admin/users/{id}/role, /admin/users/{id}/delete
  - GET/POST /admin/tests, /admin/tests/create, /admin/tests/{code}/delete
  - GET /admin/tests/{code}/questions, POST /admin/tests/{code}/questions/{id}/delete
  - GET /admin/statistics

**Сделано — Frontend (opencode):**
- `templates/admin.html` — дашборд со статистикой и карточками-ссылками
- `templates/users.html` — таблица пользователей с блокировкой/ролью/удалением
- `templates/tests.html` — список тем + форма создания
- `templates/test-questions.html` — вопросы темы с подсветкой правильного ответа
- `templates/statistics.html` — статистика по пользователям и темам
- `static/css/style.css` — + CSS (admin-grid, admin-card, admin-toolbar, table-actions, form-inline, form-select, text-muted/success/error)

**Build:** `mvn clean compile -pl web -am` — BUILD SUCCESS ✅ (76 source files)

## Session 20 — Frontend QA: Auth Fixes + User Badge | 2026-07-12

**Сделано — Первый запуск и отладка:**
- Приложение запущено, выявлены и исправлены проблемы аутентификации
- **401 на `/` без кук** — `.exceptionHandling()` с кастомным `AuthenticationEntryPoint`: API → 401, браузер → редирект на `/login`
- **401 после регистрации** — JWT-кука игнорировалась для не-API путей; добавлено явное сохранение `SecurityContext` в сессию через `HttpSessionSecurityContextRepository`
- **Логин не редиректит на `/`** — `BearerTokenAuthenticationFilter` перехватывал JWT-куку, не давая сессионной аутентификации отработать; фикс: `BearerTokenResolver` возвращает JWT только для `/api/**`
- **Старая JWT-кука → 401** — `.oauth2ResourceServer().authenticationEntryPoint()` очищает невалидную куку и редиректит на `/login`

**Сделано — Java (пользователь):**
- `CurrentUserService.getCurrentUserId(Jwt)` → `getCurrentUserId(Authentication)` — поддержка Jwt, OAuth2User, String/UserDetails
- Все контроллеры (6 файлов): `@AuthenticationPrincipal Jwt jwt` → `Authentication authentication`
- `AdminUserController` + `AdminViewController` — инжект `CurrentUserService`
- `SecurityConfig.java` — exceptionHandling, oauth2ResourceServer().authenticationEntryPoint, BearerTokenResolver (только `/api/**`)

**Сделано — Java (opencode):**
- `GlobalModelAdvice.java` — new `@ControllerAdvice`, добавляет `currentUser` (ProfileResponse) во все Thymeleaf-шаблоны

**Сделано — Frontend (opencode):**
- `layout.html` — user badge (аватар + никнейм, ссылка на /profile) в правом углу
- `style.css` — стили `.navbar-user`, `.user-badge`, `.user-avatar-small`, `.user-name`

**Build:** `mvn compile -pl web -am` — BUILD SUCCESS ✅

## Session 21 — Ручное тестирование (продолжение) + Frontend QA | 2026-07-12

**Сделано — Code Review и баг-фиксы (4):**
- **TestViewController.java** (CRITICAL) — удалён `state.moveToNextQuestion()` — `QuestionService.processAnswer()` уже вызывает его внутри. Без фикса каждый второй вопрос пропускался
- **admin.html** — исправлены ссылки на карточках статистики: "Всего тестов" и "Пройдено тестов" вели на `/admin/users` вместо `/admin/statistics`
- **users.html** — исправлены `onsubmit` с Thymeleaf-выражениями (не обрабатывались): заменены на `th:data-username` + JS `this.dataset`
- **TestViewController.getState()** — добавлена проверка `isExpired()` (была только в REST-контроллере)

**Сделано — Исправление схемы БД:**
- **`test_results`** — удалена старая колонка `topic_id` (NOT NULL), оставшаяся от предыдущей версии маппинга `@ManyToOne`. Текущий маппинг — `@ManyToMany` через join-таблицу `test_results_topics`. Из-за старой колонки INSERT падал с `"null value in column topic_id"` при сохранении результата теста

**Сделано — JSON import UI (Thymeleaf):**
- `AdminViewController.java` — `POST /admin/tests/{code}/questions/import` (MultipartFile)
- `test-questions.html` — добавлен `<input type="file" accept=".json">` + раскрывающийся блок `<details>` с примером формата JSON

**Сделано — Редактирование вопросов:**
- `AdminViewController.java` — GET/POST `/admin/tests/{code}/questions/{id}/edit`
- `question-edit.html` — новый шаблон: предзаполненные поля текста вопроса, каждого ответа в отдельном `<input>`, радио-кнопки выбора правильного, JS-кнопки «+ Добавить ответ» / «X» удалить

**Сделано — Тема тестов (чекбоксы):**
- `test-settings.html` — убран `th:checked="${topic.code() != null}"` (все темы были предотмечены, т.к. code всегда не-null)

**Сделано — Личная статистика пользователя (USER):**
- `StatisticsViewController.java` — новый `@Controller` с `GET /my-stats` (сводка, по темам, последние 20 результатов)
- `my-statistics.html` — новый шаблон: 4 карточки (всего/пройдено/не пройдено/успешность), таблица по темам, таблица последних тестов
- `layout.html` — добавлена ссылка «Моя статистика» в навбар (для всех аутентифицированных)
- `home.html` — добавлена карточка «Моя статистика»

**Сделано — Загрузка пользовательских аватаров:**
- `AvatarService.java` — полная переработка: сохранение на диск (`uploads/avatars/`), валидация типа (jpg/png/gif/svg/webp) и размера (до 2MB), удаление старого аватара
- `WebConfig.java` — новый `@Configuration`, resource handler `/uploads/avatars/**` → `file:...`
- `ViewController.java` — `POST /profile/avatar/upload` (MultipartFile) с обработкой ошибок
- `avatar-select.html` — добавлен разделитель «или загрузите свой» + форма с file input
- `SecurityConfig.java` — `/uploads/**` добавлен в `.permitAll()`
- `application.yaml` — `spring.servlet.multipart.max-file-size: 2MB`, `app.avatar.upload-dir: uploads/avatars`
- Фикс: путь резолвится относительно `user.dir` (не Tomcat temp dir)

**Build:** `mvn compile -pl web -am -q` — BUILD SUCCESS ✅ (82 source files)

## Session 22 — Ручное тестирование (продолжение) | 2026-07-14

**Bug fixes:**

| # | Проблема | Корень | Фикс |
|---|----------|--------|------|
| 1 | Пароль: неверный current → "Invalid username or password" | Переиспользование InvalidCredentialsException | ValidationException с "Current password is incorrect" |
| 2 | Блокировка: нет отдельного сообщения | DisabledException не обрабатывался | FormLoginFailureHandler (DisabledException → `/login?blocked`) |
| 3 | Nickname: скрывается при пустом значении | `<p th:if="${nickname != null}">` | Убрано if, добавлен username как fallback |
| 4 | Upload >2MB: соединение сброшено | `max-file-size: 2MB` в фильтре (до контроллера) | Поднят до 10MB, валидация в AvatarService |
| 5 | Пустые темы: 500 Internal Server Error | `@RequestParam` required=true, service не проверял | `required=false` + проверка в TestService.startTest() |
| 6 | `/test/result` до конца теста → `/test/question` | Неверный редирект | Изменён на `/test/settings` по TEST_PLAN |
| 7 | JSON ответ для MVC при CannotBlockSelf | `@RestControllerAdvice` без фильтрации | `@RestControllerAdvice(annotations = RestController.class)`, создан MvcExceptionHandler |
| 8 | Смена роли себе — срабатывает | `Long == Long` (ссылочное сравнение) | `Long.equals()` в changeUserRole() и toggleBlockUser() |
| 9 | JWT 401: "Failed to authenticate since the JWT was invalid" | NimbusJwtDecoder мог выбирать не HS512 | Явно зафиксирован HS512 в генерации и декодинге |

**Functional changes:**

- **TEST_PLAN.md** — полная переработка: Postman-инструкции, новые фазы (my-stats, JSON import, edit question, avatar upload, admin statistics, OAuth2), обновлены предусловия, исправлены имена полей (token, userId)
- **test-settings.html** — чекбоксы в CSS grid (2-3 колонки), JS-уведомление при выборе ≥2 тем
- **question.html** — "Счёт: X" → "Правильных: X из Y"
- **admin.html** — удалена дублирующая стат-карточка "Тем"
- **UserStatisticsServiceImpl + AdminStatisticsService** — комбинированные тесты исключены из per-topic расчёта
- **my-statistics.html** — добавлена заметка об исключении комбинированных тестов
- **profile.html** — nickname всегда виден (username как fallback)
- **users.html** — добавлен `<div th:if="${error}">` для flash-сообщений

**Build:** `mvn compile -q` — BUILD SUCCESS ✅

**Следующая сессия:** 23 — Auth Bug Fixes + Manual Testing

## Session 23 — Auth Bug Fixes | 2026-07-14

**Сделано — Анализ безопасности (5 критических/средних багов исправлено):**

| # | Баг | Файл | Фикс |
|---|-----|------|------|
| 1 | **Admin REST API — 403 для JWT-клиентов** | `SecurityConfig.java:156-164` | `JwtAuthenticationConverter`: маппинг `claim("role")` → `ROLE_<value>` |
| 2 | **OAuth2-админы — 403 в админку** | `CustomOAuth2UserService.java:55-59` | `List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))` вместо GitHub scopes |
| 3 | **Выбор/загрузка аватара — не сохраняется** | `UserService.java`, `UserServiceImpl.java:55-61`, `ViewController.java` | Новый `updateAvatar(userId, avatarPath)` с `@Transactional`. Контроллеры вызывают его вместо detached entity |
| 4 | **Блокировка через REST — 500 вместо 403** | `UserBlockedException.java` (new), `AuthenticationService.java:25`, `GlobalExceptionHandler.java:92` | `IllegalStateException` → `UserBlockedException`, 403 Forbidden |
| 5 | **OAuth2 cookie maxAge — 24ч (hardcode)** | `OAuth2LoginSuccessHandler.java:24-25,50` | `@Value("${jwt.expiration}")` + `expirationMs / 1000` вместо `86400` |

**Сделано — TEST_PLAN.md:**
- Phase 10: `base_url` без `/api`, явный `/api/...` во всех путях эндпоинтов

**Build:** `mvn compile -q` — BUILD SUCCESS ✅

## Session 24 — MinIO Avatar Storage | 2026-07-14

**Сделано — Зависимости и конфигурация:**
- `web/pom.xml` — добавлен `io.minio:minio:8.5.17` (официальный Java SDK для MinIO)
- `application.yaml` — добавлена секция `minio:` (endpoint, access-key, secret-key, bucket), удалён устаревший `app.avatar.upload-dir`
- `application.yaml` — добавлен `spring.jpa.open-in-view: false` (убирает warning)

**Сделано — MinIO клиент (config/MinioConfig.java):**
- `@ConfigurationProperties(prefix = "minio")` — читает endpoing/credentials/bucket из YAML
- `MinioClient` bean через builder с endpoint и credentials
- Инициализация бакета вынесена в AvatarMigrationService (чтобы избежать circular reference)

**Сделано — AvatarService переписан на MinIO:**
- Вместо `file.transferTo()` → `minioClient.putObject()` (PutObjectArgs с bucket, object name, content-type)
- Вместо `Files.deleteIfExists()` → `minioClient.removeObject()`
- Новый метод `loadAvatar(filename)` → `minioClient.getObject()` (используется AvatarProxyController)
- Сохранена валидация расширения/размера и генерация filename

**Сделано — AvatarProxyController (controller/AvatarProxyController.java, новый):**
- `@GetMapping("/uploads/avatars/{filename}")` — заменяет старый filesystem resource handler
- Загружает файл из MinIO через `avatarService.loadAvatar()`
- Отдаёт как `InputStreamResource` с правильным Content-Type (image/jpeg, image/png и т.д.)
- Cache-Control: max-age=365 дней, public (кеширование браузером)

**Сделано — WebConfig упрощён:**
- Удалён `addResourceHandlers()` с `file:` — теперь аватары отдаются через AvatarProxyController

**Сделано — AvatarService.initBucket():**
- `@PostConstruct` — создаёт bucket `avatars` в MinIO при старте приложения (если не существует)
- Инициализация встроена в AvatarService, отдельный класс миграции удалён (миграция 4 аватаров уже выполнена при первом запуске)

**Сделано — Docker и инфраструктура:**
- `docker-compose.yml` — named volumes (`postgres_data`, `minio_data`) заменены на bind mounts в `./javatraining/`
- `.gitignore` — добавлены `javatraining/` и `uploads/`

**Build:** `mvn compile -q` — BUILD SUCCESS ✅

**Тестирование:**
- MinIO контейнер запущен (порт 9000), bucket `avatars` создан автоматически при старте приложения
- 4 существующих аватара мигрированы с диска в MinIO (avatar_27, avatar_28, avatar_29, avatar_30)
- `GET /uploads/avatars/avatar_27_1783854348368.jpg` → HTTP 200, 788423 bytes, Content-Type: image/jpeg ✅
- `GET /uploads/avatars/nonexistent.jpg` → HTTP 404 ✅

## Session 25a — Project Cleanup | 2026-07-16

**Сделано — .opencode обновления:**
- `PLAN.md` — новый roadmap 25a–37 (Liquibase, Redis, удаление Thymeleaf, React, Kafka, микросервисы, ревью)
- `AGENTS.md` — обновлены правила (React-фронтенд), добавлен прогресс на 25a–37
- `CHANGELOG.md` — запись о сессии 25a

**Сделано — Очистка проекта:**
- `src/` — удалена целиком (сервлеты, JSP, старые DTO, migration tools, CSS, JS, PNG)
- `logs/` — убраны из git (git rm --cached)
- `uploads/` — убраны из git (git rm --cached)
- `questions/*.json` — перенесены в `common/src/main/resources/questions/`
- `docker/postgres/init.sql` — удалён (Legacy-схема, больше не нужна)
- `CommonApplicationTests.java` — package исправлен на `com.homeapp.javatraining`
- `WebConfig.java` — удалён (пустой @Configuration)
- `web/.../validation/` — удалена (пустая директория)
- `README.md` — переписан под текущий стек
- `.gitignore` — обновлён (добавлены `logs/`, `web/logs/`)

**Build:** `mvn install -DskipTests -q` — BUILD SUCCESS ✅
