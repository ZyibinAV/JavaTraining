# PLAN.md — План миграции JavaTraining

## Стратегия: Monolith First

1. **Фаза 0 (Сессии 0-1):** Инфраструктура, контекст, POM
2. **Фаза 1 (Сессии 2-5):** Core Migration — Spring Boot, JPA, DI
3. **Фаза 2 (Сессии 6-8):** Security — JWT, BCrypt, SecurityFilterChain
4. **Фаза 3 (Сессии 9-13):** REST API — Controllers → @RestController
5. **Фаза 4 (Сессия 14):** DTO + MapStruct
6. **Фаза 5 (Сессия 15):** Bean Validation
7. **Фаза 6 (Сессии 16-18):** Thymeleaf Frontend
8. **Фаза 7 (Сессия 19):** MinIO + Avatar Storage
9. **Фаза 8 (Сессии 20-22):** Testing
10. **Фаза 9 (Сессия 23):** Observability
11. **Фаза 10 (Сессия 24):** Финальное ревью

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

#### 🔲 Сессия 6 — BCrypt вместо SHA-256
- [ ] BCryptPasswordEncoder bean
- [ ] UserServiceImpl: BCrypt вместо PasswordUtil
- [ ] AuthenticationService: BCrypt.verify
- [ ] Удаление PasswordUtil.java

#### 🔲 Сессия 7 — JWT Token Provider
- [ ] JwtTokenProvider (генерация, валидация, парсинг)
- [ ] application.yml: jwt.secret, jwt.expiration
- [ ] AuthResponse DTO (token, userId, username, role)

#### 🔲 Сессия 8 — SecurityFilterChain
- [ ] SecurityConfig @Configuration
- [ ] JwtAuthenticationFilter extends OncePerRequestFilter
- [ ] PasswordEncoder bean
- [ ] AuthenticationManager bean
- [ ] Удаление AuthFilter, AdminFilter
- [ ] @EnableMethodSecurity + @PreAuthorize

### Фаза 3: REST API

#### 🔲 Сессия 9 — AuthController
- [ ] POST /api/auth/login
- [ ] POST /api/auth/register
- [ ] POST /api/auth/refresh
- [ ] Удаление LoginServlet, RegistrationServlet, LogoutServlet

#### 🔲 Сессия 10 — ProfileController
- [ ] GET /profile
- [ ] POST /profile/edit
- [ ] Удаление ProfileServlet, ProfileEditServlet, AvatarSelectServlet

#### 🔲 Сессия 11 — TestController (опрос)
- [ ] POST /test/start
- [ ] GET /question
- [ ] POST /question (answer)
- [ ] InterviewState: сессионный пока (HttpSession)
- [ ] Удаление StartServlet, QuestionServlet (старые)

#### 🔲 Сессия 12 — TestController (результат)
- [ ] GET /result
- [ ] TestResultService: разделение process + save
- [ ] Агрегация тем (fix мультитемных тестов)
- [ ] Удаление ResultServlet

#### 🔲 Сессия 13 — AdminControllers
- [ ] AdminUserController (CRUD, block, role)
- [ ] AdminTestController (topics CRUD, questions CRUD, JSON import)
- [ ] AdminStatisticsController
- [ ] Удаление 6 admin-сервлетов

### Фаза 4: DTO + MapStruct

#### 🔲 Сессия 14 — DTO + Mappers
- [ ] UserDTO, QuestionDTO, TopicDTO, AnswerDTO, TestResultDTO
- [ ] RegisterRequest, LoginRequest, AuthResponse
- [ ] ProfileDTO, StatsDTO
- [ ] UserMapper, QuestionMapper, TestResultMapper
- [ ] Интеграция в контроллеры

### Фаза 5: Validation

#### 🔲 Сессия 15 — Bean Validation
- [ ] @Valid + jakarta.validation на DTO
- [ ] MessageSource (русские/английские сообщения)
- [ ] Кастомные валидаторы
- [ ] Удаление ValidationFactory

### Фаза 6: Thymeleaf

#### 🔲 Сессия 16 — Layout + Auth templates
- [ ] thymeleaf-layout-dialect
- [ ] layout.html (header + footer)
- [ ] login.html, register.html
- [ ] ViewController для простых страниц

#### 🔲 Сессия 17 — Main templates
- [ ] home.html, profile.html, profile-edit.html
- [ ] avatar-select.html, test-settings.html
- [ ] question.html, result.html

#### 🔲 Сессия 18 — Admin templates
- [ ] admin.html, users.html, tests.html
- [ ] test-questions.html, statistics.html
- [ ] CSS адаптация

### Фаза 7: MinIO

#### 🔲 Сессия 19 — MinIO storage
- [ ] MinIO в docker-compose.yml
- [ ] MinIOAdapter/AvatarStorageService
- [ ] AvatarUploadController (Multipart → MinIO)
- [ ] AvatarServeController (прокси)
- [ ] Удаление AvatarUploadServlet, AvatarServeServlet

### Фаза 8: Testing

#### 🔲 Сессия 20 — Unit tests
- [ ] Сервисные тесты: @ExtendWith(MockitoExtension)
- [ ] Контроллер тесты: @WebMvcTest
- [ ] Util тесты

#### 🔲 Сессия 21 — Integration + TestContainers
- [ ] @SpringBootTest + TestContainers PostgreSQL
- [ ] Repository integration tests
- [ ] Security tests (JWT)
- [ ] MockMvc

#### 🔲 Сессия 22 — Coverage
- [ ] JaCoCo настройка
- [ ] Достижение >80% service layer

### Фаза 9: Observability

#### 🔲 Сессия 23 — Actuator + MDC
- [ ] spring-boot-starter-actuator
- [ ] Health indicators (db, minio, redis)
- [ ] Micrometer метрики
- [ ] MDC userId в логи

### Фаза 10: Финальное ревью

#### 🔲 Сессия 24 — Финальная проверка
- [ ] Чек-лист 40 review issues
- [ ] mvn clean verify — зелёный билд
- [ ] Обновление README

---

## Чек-лист исправления замечаний ревью

### ERROR (13 шт) — все должны быть исправлены

| # | Файл | Строка | Сессия | Статус |
|---|------|--------|--------|--------|
| 1 | PasswordUtil.java | 15 | 6 | 🔲 |
| 2 | ProfileEditServlet.java | 44 | 10 | 🔲 |
| 3 | ProfileEditServlet.java | 45 | 10 | 🔲 |
| 4 | ResultServlet.java | 55 | 12 | 🔲 |
| 5 | AvatarUploadServlet.java | 52 | 19 | 🔲 |
| 6 | AdminBlockUserServlet.java | 27 | 13 | 🔲 |
| 7 | AdminChangeRoleServlet.java | 31 | 13 | 🔲 |
| 8 | AdminUserService.java | 40 | 8 | 🔲 |
| 9 | ResultServlet.java | 46 | 12 | 🔲 |
| 10 | AdminStatisticsService.java | 1 | 4 | ✅ |
| 11 | BaseServlet.java | 91 | 4 | ✅ |
| 12 | AuthenticationService.java | 1 | 4 | ✅ |
| 13 | AdminStatisticsService.java | 20 | 13 | 🔲 |

### WARNING (20 шт)

| # | Файл | Строка | Сессия | Статус |
|---|------|--------|--------|--------|
| 1 | BaseServlet.java | 24 | 23 | ✅ |
| 2 | BaseServlet.java | 45 | 4 | ✅ |
| 3 | BaseServlet.java | 62 | 4 | ✅ |
| 4 | LoginServlet.java | 34 | 9 | 🔲 |
| 5 | ProfileEditServlet.java | 48 | 10 | 🔲 |
| 6 | QuestionServlet.java | 71 | 11 | 🔲 |
| 7 | QuestionServlet.java | 79 | 11 | 🔲 |
| 8 | ResultServlet.java | 62 | 12 | 🔲 |
| 9 | AvatarUploadServlet.java | 56 | 19 | 🔲 |
| 10 | AdminUserServlet.java | 82 | 13 | 🔲 |
| 11 | AdminTestServlet.java | 262 | 13 | 🔲 |
| 12 | QuestionService.java | 45 | 11 | 🔲 |
| 13 | TestResultService.java | 30 | 12 | 🔲 |
| 14 | StartServlet.java | 46 | 11 | 🔲 |
| 15 | QuestionValidator.java | 36 | 15 | 🔲 |
| 16 | QuestionServlet.java | 35 | 11 | 🔲 |
| 17 | AdminUserServlet.java | 56 | 13 | 🔲 |
| 18 | AvatarSelectServlet.java | 51 | 19 | 🔲 |
| 19 | ProfileEditServlet.java | 44 (old) | 10 | 🔲 |
| 20 | AdminUserServlet.java | 82 (duplicate) | 13 | 🔲 |

### INFO (7 шт)

| # | Файл | Строка | Сессия | Статус |
|---|------|--------|--------|--------|
| 1 | UserValidation.java | 19 | 15 | 🔲 |
| 2 | BaseStatsTest.java | 208 | 21 | 🔲 |
| 3 | ErrorHandlerTest.java | 90 | 20 | 🔲 |
| 4 | RequestHandlerTest.java | 55 | 20 | 🔲 |
| 5 | DtoStatsTest.java | 70 | 20 | 🔲 |
| 6 | SessionUtils.java | 10 | 11 | 🔲 |
| 7 | UserValidation.java | class | 15 | 🔲 |

---

## Соглашения

- Коммиты: `session-N: краткое описание` (например, `session-6: replace SHA-256 with BCrypt`)
- Язык общения: русский
- Код: без комментариев (кроме javadoc где нужно)
- После каждой сессии: AGENTS.md (прогресс) + CHANGELOG.md + коммит
