# PLAN.md — План миграции JavaTraining

## Стратегия: Monolith First

1. **Фаза 0 (Сессии 0-1):** Инфраструктура, контекст, POM
2. **Фаза 1 (Сессии 2-5):** Core Migration — Spring Boot, JPA, DI
3. **Фаза 2 (Сессии 6-9):** Security — JWT, BCrypt, OAuth2 Resource Server, GitHub OAuth2 Client
4. **Фаза 3 (Сессии 10-14):** REST API — Controllers → @RestController
5. **Фаза 4 (Сессия 15):** DTO + MapStruct
6. **Фаза 5 (Сессия 16):** Bean Validation
7. **Фаза 6 (Сессии 17-19):** Thymeleaf Frontend
8. **Фаза 7 (Сессии 21-23):** Testing (manual) + Bug fixes
9. **Фаза 8 (Сессия 24):** MinIO Avatar Storage
10. **Фаза 9 (Сессии 25-27):** Unit + Integration tests, Coverage
11. **Фаза 10 (Сессия 28):** Observability
12. **Фаза 11 (Сессия 29):** Финальное ревью

---

## Детальный план по сессиям

### Фаза 0: Инфраструктура

#### ✅ Сессия 0 — Подготовка контекста (2026-07-04)
- [x] AGENTS.md — архитектура, роли, глоссарий, контекст
- [x] PLAN.md — этот план
- [x] CHANGELOG.md — пустой, под записи

#### ✅ Сессия 1 — POM + модули (2026-07-05)
- [x] parent POM (packaging pom) с spring-boot-starter-parent
- [x] Модули: `common` (shared DTO/events), `web` (основное приложение)
- [x] Все зависимости: spring-boot-starter-web, data-jpa, security, thymeleaf, validation, actuator
- [x] Lombok + MapStruct 1.6.3 annotation processors
- [x] Удаление jakarta-servlet-api, jstl, hibernate-core (старый), log4j2, jackson-databind, h2, HikariCP
- [x] Docker compose: PostgreSQL, MinIO, Kafka + ZooKeeper

### Фаза 1: Core Migration

#### ✅ Сессия 2 — application.yml + Spring Boot config (2026-07-05)
- [x] application.yml (datasource, jpa, hikari, logging)
- [x] Удаление hibernate.cfg.xml, HibernateUtil
- [x] Настройка Logback (замена log4j2.xml)
- [x] Spring Boot main class

#### ✅ Сессия 3 — Spring Data JPA репозитории (2026-07-05)
- [x] UserRepository extends JpaRepository
- [x] QuestionRepository extends JpaRepository
- [x] TopicRepository extends JpaRepository
- [x] TestResultRepository extends JpaRepository
- [x] Удаление 5 Hibernate*Repository классов + 4 интерфейсов

#### ✅ Сессия 4 — @Service + DI (2026-07-05)
- [x] Все сервисы: @Service, @RequiredArgsConstructor
- [x] Сервисные интерфейсы @Service (или удаление если 1 impl)
- [x] Удаление ApplicationConfig, AppInitListener
- [x] Удаление BaseServlet (ручной DI)

#### ✅ Сессия 5 — Global Exception Handler (2026-07-07)
- [x] @ControllerAdvice + @ExceptionHandler
- [x] ErrorResponse DTO
- [x] Удаление ErrorHandler, RequestHandler
- [x] Адаптация ValidationException, AuthenticationException
- [x] Per-service исключения (user, topic, question packages)
- [x] ApiException abstract base

### Фаза 2: Security

#### ✅ Сессия 6 — BCrypt вместо SHA-256 (2026-07-07)
- [x] BCryptPasswordEncoder bean (SecurityConfig)
- [x] UserServiceImpl: BCrypt вместо PasswordUtil
- [x] Удаление PasswordUtil.java из web-модуля
- [ ] AuthenticationService: BCrypt.verify _(отложено — будет переписан в Spring Security сессиях)_

#### ✅ Сессия 7 — JWT Token Provider (2026-07-07)
- [x] JwtTokenProvider (генерация, валидация, парсинг)
- [x] application.yml: jwt.secret, jwt.expiration
- [x] AuthResponse DTO (token, userId, username, role)

#### ✅ Сессия 8 — SecurityFilterChain + OAuth2 Resource Server (2026-07-07)
- [x] SecurityConfig: `oauth2ResourceServer().jwt()` вместо кастомного JwtAuthenticationFilter
- [x] AuthenticationManager bean
- [x] @EnableMethodSecurity + @PreAuthorize
- [x] application.yml: spring.security.oauth2.resourceserver.jwt
- [x] Удаление AuthFilter, AdminFilter

#### ✅ Сессия 9 — OAuth2 Client (GitHub Login) (2026-07-07)
- [x] spring-boot-starter-oauth2-client
- [x] application.yml: spring.security.oauth2.client.registration.github
- [x] CustomOAuth2UserService (создание/привязка User к GitHub account)
- [x] OAuth2LoginSuccessHandler (выдача JWT после GitHub логина)
- [x] Login page: добавлена кнопка "Sign in with GitHub"

### Фаза 3: REST API

#### ✅ Сессия 10 — AuthController + Two-Token Pattern (2026-07-07)
- [x] POST /api/auth/login (access token JSON + refresh token HttpOnly cookie)
- [x] POST /api/auth/register (access token JSON + refresh token HttpOnly cookie)
- [x] POST /api/auth/refresh (cookie → validate+rotate → новый access + refresh)
- [x] RefreshToken entity + repository (common module)
- [x] RefreshTokenService (create, validateAndRotate с token rotation)
- [x] InvalidRefreshTokenException → GlobalExceptionHandler (401)
- [x] application.yaml: access 15min, refresh 30d
- [x] Удаление LoginServlet, RegistrationServlet, LogoutServlet

#### ✅ Сессия 11 — ProfileController + Code Improvements (2026-07-08)
- [x] GET /api/profile (ProfileResponse + UserMapper)
- [x] POST /api/profile (update nickname/about)
- [x] POST /api/profile/password (BCrypt change password, confirmPassword validation)
- [x] userRepository.save() removal + @Transactional on write methods
- [x] UserRepository out of controller → UserService.getProfile()
- [x] CurrentUserService (userId from JWT, DRY)
- [x] Business checks moved from controller to service layer
- [x] handleUnexpected → "Internal server error" (no leak)
- [x] User.equals/hashCode on id (instead of username)
- [x] Удаление ProfileServlet, ProfileEditServlet, AvatarSelectServlet

#### ✅ Сессия 12 — TestController (опрос) (2026-07-08)
- [x] POST /api/test/start (TestStartRequest → TestService.startTest → InterviewState в HttpSession)
- [x] GET /api/test/question (InterviewState из сессии → QuestionResponse)
- [x] POST /api/test/question (AnswerRequest → TestService.processAnswer → AnswerResultResponse с nextQuestion)
- [x] InterviewState: сессионный пока (HttpSession, IF_REQUIRED в SecurityConfig)
- [x] TestService — выделение бизнес-логики из контроллера
- [x] SecurityConfig: sessionCreationPolicy STATELESS → IF_REQUIRED
- [x] TopicLoader.findByCode: orElse(null) → orElseThrow(TopicNotFoundException) (фикс #14)
- [x] Удаление StartServlet, QuestionServlet (старые)
- [x] DTO: TestStartRequest, AnswerRequest, AnswerItem, QuestionResponse, AnswerResultResponse, AnswerResult

#### ✅ Сессия 13 — TestController (результат) (2026-07-10)
- [x] GET /api/test/result
- [x] TestResultService: разделение processResult + saveResult
- [x] Агрегация тем (@ManyToMany Set<Topic>, fix #4)
- [x] Удаление ResultServlet

#### ✅ Сессия 14 — AdminControllers (2026-07-11)
- [x] AdminUserController (CRUD, block, role)
- [x] AdminTopicController (topics CRUD, questions CRUD, JSON import)
- [x] AdminStatisticsController
- [x] Удаление 6 admin-сервлетов

### Фаза 4: DTO + MapStruct

#### ✅ Сессия 15 — DTO + Mappers (2026-07-11)
- [x] UserDTO, TopicDTO, AnswerDTO, QuestionDTO, TestResultDTO
- [x] TopicMapper, AnswerMapper, QuestionMapper, TestResultMapper
- [x] UserMapper дополнен (toUserDTO)
- [x] AdminUserController — User → UserDTO
- [x] AdminTopicController — GET endpoints → TopicDTO/QuestionDTO
- [x] QuestionResponse — AnswerItem → AnswerDTO
- [x] TestController — AnswerItem → AnswerDTO
- [x] AnswerItem.java удалён

### Фаза 5: Validation

#### ✅ Сессия 16 — Bean Validation (2026-07-11)
- [x] @Valid + jakarta.validation на DTO (9 request DTO)
- [x] MessageSource (messages.properties + application.yaml)
- [x] Кастомные валидаторы — не понадобились (cross-field в сервисах)
- [x] Удаление UserValidation.java, QuestionValidator.java, ValidationFactory.java

### Фаза 6: Thymeleaf

#### ✅ Сессия 17 — Layout + Auth templates (2026-07-11)
- [x] thymeleaf-layout-dialect + thymeleaf-extras-springsecurity6
- [x] layout.html (header + footer с навигацией)
- [x] login.html (форма + GitHub OAuth2), register.html
- [x] ViewController (login, register GET/POST) + CustomUserDetailsService + FormLoginSuccessHandler
- [x] SecurityConfig: formLogin, logout, permitAll /register/** /css/**

#### ✅ Сессия 18 — Main templates (2026-07-11)
- [x] home.html, profile.html, profile-edit.html
- [x] avatar-select.html, test-settings.html
- [x] question.html, result.html
- [x] Java: ViewController (+6 routes) + TestViewController (new, 5 routes)
- [x] CSS: profile, avatar grid, progress bar, question, result styles

#### ✅ Сессия 19 — Admin templates (2026-07-11)
- [x] admin.html, users.html, tests.html
- [x] test-questions.html, statistics.html
- [x] CSS адаптация (admin-grid, admin-card, form-inline, form-select, table-actions)
- [x] AdminViewController.java (Java, 10 методов)
- [x] Полный CRUD для пользователей, тем, вопросов + статистика

### Фаза 7: Manual Testing

#### ✅ Сессия 21 — Manual testing (Phase 1) (2026-07-12)
- [x] Code review — 4 бага исправлено (double moveToNextQuestion, admin.html ссылки, users.html onsubmit, isExpired в getState())
- [x] Schema fix: test_results DROP COLUMN topic_id
- [x] JSON import UI (Thymeleaf)
- [x] Edit question UI (question-edit.html)
- [x] Тест-сеттинг: чекбоксы не отмечены по умолчанию
- [x] User statistics page (/my-stats)
- [x] Custom avatar upload (AvatarService, WebConfig)
- [x] Ссылка «Моя статистика» в навбаре и на главной

#### ✅ Сессия 22 — Manual testing (Phase 2) (2026-07-14)
- [x] TEST_PLAN.md полная переработка (Postman, новые фазы)
- [x] Login blocked user: отдельное сообщение (FormLoginFailureHandler)
- [x] Profile nickname: username fallback (не прятать строку)
- [x] Upload >2MB: адекватная ошибка (resolve-lazily / max 10MB)
- [x] Empty topics validation в TestService (не в контроллере)
- [x] /test/result → /test/settings (если тест не завершён)
- [x] Error messages center alignment
- [x] GlobalExceptionHandler → только @RestController (+ MvcExceptionHandler)
- [x] AdminUserService: Long.equals() вместо ==
- [x] JWT: HS512 явно (алгоритм mismatch fix)
- [x] Per-topic stats: комбинированные тесты исключены
- [x] Test settings: CSS grid для чекбоксов
- [x] Question page: "Правильных: X из Y"
- [x] Admin dashboard: убрана стат-карточка "Тем"
- [x] Уведомления о комбинированных тестах (JS + text)

#### ✅ Сессия 23 — Auth Bug Fixes (2026-07-14)
- [x] JwtAuthenticationConverter: маппинг claim("role") → ROLE_<value> (SecurityConfig)
- [x] CustomOAuth2UserService: authorities из user.getRole() вместо GitHub scopes
- [x] UserService.updateAvatar() + фикс detached entity в ViewController
- [x] AuthenticationService: UserBlockedException вместо IllegalStateException
- [x] OAuth2LoginSuccessHandler: cookie maxAge из конфига вместо 86400
- [x] TEST_PLAN.md: Phase 10 — явный /api/ в путях

### Фаза 8: MinIO Avatar Storage

#### ✅ Сессия 24 — MinIO Avatar Storage (2026-07-14)
- [x] MinIO client dependency (io.minio:minio:8.5.17)
- [x] MinioConfig: @ConfigurationProperties + MinioClient bean
- [x] AvatarService: MinIO storage (putObject/removeObject/getObject) вместо filesystem
- [x] docker-compose.yml: bind mount ./javatraining/minio вместо named volume, конфигурация проверена
- [x] AvatarProxyController: proxy-эндпоинт для отдачи аватаров из MinIO (вместо WebConfig resource handler)
- [x] AvatarService.initBucket(): @PostConstruct создание бакета при старте (без отдельного класса)
- [x] WebConfig: удалён filesystem resource handler
- [x] .gitignore: добавлены javatraining/ и uploads/

### Фаза 9: Project Cleanup

#### ✅ Сессия 25a — Project Cleanup (2026-07-16)
- [x] Удаление `src/` (сервлеты, JSP, старые DTO, migration tools, CSS, JS, PNG)
- [x] `logs/`, `uploads/` — убрать из git (git rm --cached)
- [x] Перенос `questions/*.json` → `common/src/main/resources/questions/`
- [x] Удаление `docker/postgres/init.sql`
- [x] Исправление package в common тестах
- [x] Удаление пустых WebConfig.java, validation/
- [x] Переписать README.md под текущий стек
- [x] Проверка .gitignore

### Фаза 10: Liquibase

#### ✅ Сессия 26 — Liquibase миграции (2026-07-16)
- [x] Добавление `liquibase-core` и `liquibase-maven-plugin` в POM
- [x] Создание `db/changelog/db.changelog-master.yaml`
- [x] Создание `v001-init-schema.yaml` (8 changeset'ов: 7 таблиц + join-таблица)
- [x] Переключение `ddl-auto: update` → `validate`
- [x] Удаление init.sql из docker-compose
- [x] Переименование `javatraining/` → `docker/volumes/` (volumes data)
- [x] Тестирование: Liquibase создаёт таблицы при старте ✅

### Фаза 11: Redis

#### 🔲 Сессия 27 — Redis Session State
- [ ] Добавление `spring-session-data-redis` + `lettuce-core`
- [ ] Конфигурация Redis в docker-compose и application.yaml
- [ ] Перенос InterviewState из HttpSession в Redis
- [ ] Настройка `@EnableRedisHttpSession`
- [ ] Docker: добавление контейнера Redis

### Фаза 12: Удаление Thymeleaf

#### 🔲 Сессия 28 — Thymeleaf → REST only
- [ ] Удаление `thymeleaf`, `thymeleaf-layout-dialect`, `thymeleaf-extras-springsecurity6` из POM
- [ ] Удаление `templates/` (14 .html файлов)
- [ ] Удаление `static/` (CSS, JS)
- [ ] Удаление ViewController, TestViewController, AdminViewController, StatisticsViewController, GlobalModelAdvice
- [ ] Удаление FormLoginSuccessHandler, CustomUserDetailsService, MvcExceptionHandler
- [ ] SecurityConfig: удаление formLogin(), oauth2Login(), настройка STATELESS + CORS
- [ ] AvatarProxyController: переход на REST

### Фаза 13: React Frontend

#### 🔲 Сессия 29 — React Auth + Profile
- [ ] Инициализация React (Vite + TypeScript)
- [ ] Настройка прокси (vite.config.ts → localhost:8080)
- [ ] React Router: /login, /register, /profile
- [ ] Страницы: Login, Register, Profile
- [ ] API service layer (axios)

#### 🔲 Сессия 30 — React Tests + Admin
- [ ] Компоненты: TestSettings, Question, Result, MyStats
- [ ] Admin: Users, Topics, Questions, Statistics
- [ ] Тесты: Vitest + React Testing Library
- [ ] Проверка маршрутов и навигации

### Фаза 14: Kafka

#### 🔲 Сессия 31 — Kafka Event Bus
- [ ] Добавление `spring-kafka` в POM
- [ ] Создание топиков (user-events, test-events)
- [ ] Продюсеры: UserEventProducer, TestEventProducer
- [ ] Консьюмеры: обработка событий
- [ ] Тестирование с TestContainers Kafka

### Фаза 15: Микросервисы

#### 🔲 Сессия 32 — Eureka + Gateway
- [ ] Создание модуля `gateway` (Spring Cloud Gateway)
- [ ] Создание модуля `discovery` (Eureka Server)
- [ ] Настройка маршрутов: auth → 8081, user → 8082, test → 8083, admin → 8084
- [ ] docker-compose: добавление Eureka, Gateway

#### 🔲 Сессия 33 — auth-service (8081)
- [ ] Выделение AuthController, JwtTokenProvider, RefreshToken
- [ ] Настройка security только для auth
- [ ] Подключение к общей БД (пока единая)

#### 🔲 Сессия 34 — user-service (8082)
- [ ] Выделение ProfileController, UserService, AvatarService
- [ ] Kafka consumer для user-events
- [ ] REST client для auth-service (JWT validation)

#### 🔲 Сессия 35 — test-service (8083)
- [ ] Выделение TestController, TestService, TestResultService
- [ ] Kafka consumer для test-events
- [ ] REST client для user-service

#### 🔲 Сессия 36 — admin-service + интеграция (8084)
- [ ] Выделение AdminUserController, AdminTopicController, AdminStatisticsController
- [ ] Gateway security: Centralized JWT validation
- [ ] Интеграционное тестирование всех сервисов

### Фаза 16: Финальное ревью

#### 🔲 Сессия 37 — Final Review
- [ ] Чек-лист финальной проверки (40 пунктов)
- [ ] `mvn clean verify` — зелёный билд
- [ ] Обновление README
- [ ] Финальный коммит

---

## Чек-лист исправления замечаний ревью

### ERROR (13 шт) — все должны быть исправлены

| # | Файл | Строка | Сессия | Статус |
|---|------|--------|--------|--------|
| 1 | PasswordUtil.java | 15 | 6 | ✅ |
| 2 | ProfileEditServlet.java | 44 | 11 | ✅ |
| 3 | ProfileEditServlet.java | 45 | 11 | ✅ |
| 4 | ResultServlet.java | 55 | 13 | ✅ |
| 5 | AvatarUploadServlet.java | 52 | 20 | 🔲 |
| 6 | AdminBlockUserServlet.java | 27 | 14 | ✅ |
| 7 | AdminChangeRoleServlet.java | 31 | 14 | ✅ |
| 8 | AdminUserService.java | 40 | 14 | ✅ |
| 9 | ResultServlet.java | 46 | 13 | ✅ |
| 10 | AdminStatisticsService.java | 1 | 4 | ✅ |
| 11 | BaseServlet.java | 91 | 4 | ✅ |
| 12 | AuthenticationService.java | 1 | 4 | ✅ |
| 13 | AdminStatisticsService.java | 20 | 14 | 🔲 |

### WARNING (21 шт)

| # | Файл | Строка | Сессия | Статус |
|---|------|--------|--------|--------|
| 1 | BaseServlet.java | 24 | 24 | ✅ |
| 2 | BaseServlet.java | 45 | 4 | ✅ |
| 3 | BaseServlet.java | 62 | 4 | ✅ |
| 4 | LoginServlet.java | 34 | 10 | ✅ |
| 5 | ProfileEditServlet.java | 48 | 11 | 🔲 |
| 6 | QuestionServlet.java | 71 | 12 | ✅ |
| 7 | QuestionServlet.java | 79 | 12 | ✅ |
| 8 | ResultServlet.java | 62 | 13 | ✅ |
| 9 | AvatarUploadServlet.java | 56 | 20 | 🔲 |
| 10 | AdminUserServlet.java | 82 | 14 | ✅ |
| 11 | AdminTestServlet.java | 262 | 14 | ✅ |
| 12 | QuestionService.java | 45 | 12 | 🔲 |
| 13 | TestResultService.java | 30 | 13 | ✅ |
| 14 | StartServlet.java | 46 | 12 | ✅ |
| 15 | QuestionValidator.java | 36 | 16 | 🔲 |
| 16 | QuestionServlet.java | 35 | 12 | ✅ |
| 17 | AdminUserServlet.java | 56 | 14 | ✅ |
| 18 | AvatarSelectServlet.java | 51 | 20 | 🔲 |
| 19 | ProfileEditServlet.java | 44 (old) | 11 | ✅ |
| 20 | AdminUserServlet.java | 82 (dup) | 14 | ✅ |
| 21 | TopicLoader.java | 21 | 12 | ✅ |

### INFO (7 шт)

| # | Файл | Строка | Сессия | Статус |
|---|------|--------|--------|--------|
| 1 | UserValidation.java | 19 | 16 | 🔲 |
| 2 | BaseStatsTest.java | 208 | 22 | 🔲 |
| 3 | ErrorHandlerTest.java | 90 | — | ✅ |
| 4 | RequestHandlerTest.java | 55 | — | ✅ |
| 5 | DtoStatsTest.java | 70 | 21 | 🔲 |
| 6 | SessionUtils.java | 10 | 12 | 🔲 |
| 7 | UserValidation.java | class | 16 | 🔲 |

---

## Соглашения

- Коммиты: `session-N: краткое описание` (например, `session-6: replace SHA-256 with BCrypt`)
- Язык общения: русский
- Код: без комментариев (кроме javadoc где нужно)
- После каждой сессии: AGENTS.md (прогресс) + CHANGELOG.md + коммит
