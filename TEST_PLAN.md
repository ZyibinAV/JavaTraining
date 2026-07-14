# План ручного тестирования JavaTraining

## Предусловия

1. `docker compose up -d postgres` — PostgreSQL 16 запущен
2. Приложение запущено: `mvn spring-boot:run` или `java -jar web/target/*.war`
3. БД создаётся автоматически (ddl-auto: update). **Опционально:** 9 тем и 669 вопросов предзагружены (импорт из JSON через админку или скрипт)
4. Schema fix выполнена: `test_results` НЕ содержит колонки `topic_id`, связь M:N через `test_results_topics`
5. Директория `uploads/avatars/` существует и доступна для записи
6. Multipart configured: max file size 2MB
7. Браузер открыт на `http://localhost:8080`
8. Для REST-тестов: curl / Postman / любая консоль
9. Для OAuth2: `GITHUB_CLIENT_ID` / `GITHUB_CLIENT_SECRET` в переменных окружения или application.yaml

---

## Фаза 0 — Проверка окружения

| # | Шаг | Ожидаемый результат |
|---|------|---------------------|
| 0.1 | `docker ps` — PostgreSQL в списке | postgres:16 запущен, порт 5432 |
| 0.2 | Открыть `http://localhost:8080` без кук | Редирект на `/login` |
| 0.3 | `http://localhost:8080/login` | Страница входа (форма username/password, ссылка на регистрацию, кнопка GitHub OAuth2) |
| 0.4 | `http://localhost:8080/uploads/avatars/` | Статика отдаётся (список файлов или 403 — зависит от настройки) |

---

## Фаза 1 — Подготовка данных (ADMIN)

Первый зарегистрированный пользователь получает роль ADMIN. Если БД чистая — создать темы и вопросы вручную. Если предзагружена — пропустить создание, перейти к проверке.

| # | Шаг | Ожидаемый результат |
|---|------|---------------------|
| 1.1 | `/register`, заполнить `admin` / `admin123` / `admin@test.com` | Редирект на `/`, навбар: user badge (admin) + Выйти, кнопка Админка |
| 1.2 | `/admin` | Dashboard: 4 stat-карточки + 3-4 карточки навигации (Users, Tests, Statistics) |
| 1.3 | `/admin/tests` → поле Код: `java-core`, Название: `Java Core` → Создать | Тема в таблице |
| 1.4 | `/admin/tests` → ещё 1-2 темы (если нужно) | Темы в таблице |
| 1.5 | `/admin/tests/java-core/questions` → форма создания вопроса | Форма + форма импорта JSON + список вопросов |
| 1.6 | Создать 5+ вопросов (текст, ответы через \|, индекс прав. ответа) | Вопросы в списке, правильный ответ выделен зелёным |

**Примеры вопросов для java-core:**

| Вопрос | Ответы | Индекс |
|--------|--------|--------|
| What is JVM? | Interpreter for bytecode \| Compiler \| Database \| IDE | 0 |
| What is the entry point of a Java program? | main() \| start() \| init() \| run() | 0 |
| Which keyword is used to inherit a class? | extends \| implements \| inherits \| super | 0 |
| What is the default value of a boolean? | false \| true \| null \| 0 | 0 |
| Which package contains List interface? | java.util \| java.lang \| java.io \| java.sql | 0 |

---

## Фаза 2 — Регистрация

| # | Шаг | Ожидаемый результат |
|---|------|---------------------|
| 2.1 | `/register`, заполнить `user1` / `pass123` / `user1@test.com` → отправить | Редирект на `/`; навбар: user badge (user1) + Выйти (нет Вход/Регистрация) |
| 2.2 | `/register` с существующим username `user1` | Ошибка валидации, форма не очищена |
| 2.3 | `/register` с существующим email `user1@test.com` | Ошибка валидации |
| 2.4 | `/register`, пароль `12345` (< 6 символов) | Ошибка валидации |

---

## Фаза 3 — Вход / Выход

| # | Шаг | Ожидаемый результат |
|---|------|---------------------|
| 3.1 | `/login`, username=`user1`, password=`pass123` | Редирект на `/`, user badge с `user1`, JWT-кука **НЕ устанавливается** (только для REST) |
| 3.2 | `/login`, username=`user1`, password=`wrongpass` | `/login?error`, сообщение об ошибке |
| 3.3 | Заблокировать user1 через админку → `/login` user1 | "User is blocked" или ошибка аутентификации |
| 3.4 | Разблокировать user1 → login → нажать «Выйти» | `/login?logout`, сессия завершена |
| 3.5 | После выхода открыть `/` | Редирект на `/login` |

---

## Фаза 4 — Профиль

| # | Шаг | Ожидаемый результат |
|---|------|---------------------|
| 4.1 | Войти как user1 → `/profile` | username, email, аватар (по умолчанию #1), nickname, about, дата регистрации |
| 4.2 | `/profile/edit` → nickname=`User One`, about=`Hello!` → Сохранить | Редирект на `/profile`, nickname и about обновлены |
| 4.3 | `/profile/edit` → очистить nickname → Сохранить | Nickname пуст (отображается username) |
| 4.4 | `/profile/avatar` → выбрать аватар #5 | Редирект на `/profile`, аватар обновлён на #5 |
| 4.5 | `/profile/avatar` → загрузить свой файл (jpg/png, < 2MB) | Аватар обновлён на загруженный, путь `/uploads/avatars/avatar_...` |
| 4.6 | `/profile/avatar` → загрузить файл > 2MB | Ошибка: "Файл слишком большой" или 400 |
| 4.7 | `/profile/avatar` → загрузить файл недопустимого формата (.exe, .pdf) | Ошибка: "Недопустимый формат файла" |
| 4.8 | `/profile/password` → current=`pass123`, new=`newpass123`, confirm=`newpass123` | "Пароль успешно изменён" |
| 4.9 | `/profile/password` → current=`wrongpass`, new=`newpass123`, confirm=`newpass123` | Ошибка "Неверный текущий пароль" |
| 4.10 | `/profile/password` → current=`pass123`, new=`newpass123`, confirm=`pass456` | Ошибка "Пароли не совпадают" |

---

## Фаза 5 — Тестирование (Thymeleaf)

**Важно:** Перед тестированием убедиться, что в БД есть тема `java-core` с 5+ вопросами.

| # | Шаг | Ожидаемый результат |
|---|------|---------------------|
| 5.1 | `/test/settings` | Чекбоксы тем **НЕ отмечены** по умолчанию, селект 5/10/15/20 |
| 5.2 | Отметить "Java Core", выбрать "5" → Начать тест | Редирект на `/test/question`, progress bar 0/5, вопрос №1 из 5 |
| 5.3 | Ответить на вопрос → «Ответить» | Следующий вопрос, progress bar +1 |
| 5.4 | Ответить на все 5 вопросов | Редирект на `/test/result` |
| 5.5 | Результат: % score, correct/total, badge зелёный (>=50%) или красный | Score = (правильные/5)*100 |
| 5.6 | «Пройти ещё раз» | `/test/settings` |
| 5.7 | Открыть `/test/question` без активной сессии | Редирект на `/test/settings` |
| 5.8 | Открыть `/test/result` до завершения теста | Редирект на `/test/settings` |
| 5.9 | Снять все темы → Начать тест | Ошибка (нет выбранных тем) |
| 5.10 | Выбрать несколько тем (если есть 2+ темы с вопросами) | Вопросы из всех выбранных тем |
| 5.11 | Выбрать 20 вопросов при 5 доступных | Ошибка "Недостаточно вопросов" |

---

## Фаза 6 — Моя статистика

| # | Шаг | Ожидаемый результат |
|---|------|---------------------|
| 6.1 | Войти как user1 → `/my-stats` | 4 stat-карточки (все 0 если тестов нет), кнопка "Пройти тест" |
| 6.2 | Пройти 2+ теста → `/my-stats` | Total = 2, Passed = X, Failed = Y, Success% = Z |
| 6.3 | Таблица "По темам" | Для каждой темы: кол-во тестов, пройдено, успешность |
| 6.4 | Таблица "Последние тесты" | 2 строки с датой, темами, счётом (correct/total), статус (Пройден/Не пройден) |
| 6.5 | Перейти по ссылке «Моя статистика» из навбара | Редирект на `/my-stats` |
| 6.6 | Перейти по карточке «Моя статистика» с главной | Редирект на `/my-stats` |

---

## Фаза 7 — Админка (USER → 403)

| # | Шаг | Ожидаемый результат |
|---|------|---------------------|
| 7.1 | Выйти из ADMIN, войти как user1 → `/admin` | 403 Forbidden |
| 7.2 | Войти как ADMIN → `/admin` | Dashboard: карточки статистики, ссылки на Users, Tests, Statistics |
| 7.3 | `/admin/users` | Таблица: admin (ADMIN), user1 (USER) |
| 7.4 | Заблокировать user1 → save | Статус user1 = "Заблокирован" |
| 7.5 | Разблокировать user1 | Статус = "Активен" |
| 7.6 | Сменить роль user1 → ADMIN | Роль user1 = ADMIN |
| 7.7 | Сменить роль user1 → USER | Роль user1 = USER |
| 7.8 | **Попытка заблокировать себя (admin)** | Ошибка (нельзя заблокировать себя) |
| 7.9 | **Попытка сменить роль себе (admin)** | Ошибка (нельзя сменить роль себе) |
| 7.10 | Удалить user1 → confirm диалог | Пользователь удалён из таблицы |
| 7.11 | `/admin/tests` → удалить тему | Тема удалена (вопросы и связи каскадно) |

---

## Фаза 8 — Админка: JSON Import + Edit Question UI

| # | Шаг | Ожидаемый результат |
|---|------|---------------------|
| 8.1 | `/admin/tests/{code}/questions` — секция "Импорт из JSON" | Форма с file input + кнопка "Импортировать" |
| 8.2 | Нажать на "Формат JSON (нажмите для примера)" | Раскрывается `<details>` с примером JSON |
| 8.3 | Загрузить валидный JSON-файл с 3 вопросами | 3 вопроса добавлены в таблицу |
| 8.4 | Загрузить JSON с пустым вопросом | Ошибка валидации |
| 8.5 | Загрузить JSON с неверным `correctAnswerIndex` | Ошибка валидации |
| 8.6 | Загрузить не-JSON файл (.txt) | Ошибка |
| 8.7 | Нажать "Редактировать" на вопросе | `/admin/tests/{code}/questions/{id}/edit` с формой |
| 8.8 | Изменить текст вопроса → Сохранить | Вопрос обновлён, редирект на список |
| 8.9 | Изменить правильный ответ (другой radio) → Сохранить | Правильный ответ изменён |
| 8.10 | Нажать "+ Добавить ответ" → ввести текст → Сохранить | Ответ добавлен |
| 8.11 | Удалить ответ (кнопка X) → Сохранить | Ответ удалён |
| 8.12 | Удалить вопрос через кнопку "Удалить" | Confirm диалог → вопрос удалён |

---

## Фаза 9 — Админка: Statistics

| # | Шаг | Ожидаемый результат |
|---|------|---------------------|
| 9.1 | `/admin/statistics` | 4 stat-карточки (Total/Passed/Failed/Success%) |
| 9.2 | Таблица "По пользователям" | Каждый пользователь с кол-вом тестов, пройдено, успешность |
| 9.3 | Таблица "По темам" | Каждая тема с кол-вом тестов, пройдено, успешность |
| 9.4 | Пройти тест от user1 → `/admin/statistics` | Данные обновились |

---

## Фаза 10 — REST API (Postman)

### Настройка окружения Postman

Создайте окружение Postman со следующими переменными:

| Variable | Initial Value | Описание |
|----------|--------------|----------|
| `base_url` | `http://localhost:8080` | Базовый URL (без `/api`) |
| `admin_token` | | Токен ADMIN (заполнится автоматически) |
| `user_token` | | Токен USER (заполнится автоматически) |
| `admin_id` | | ID администратора |
| `user_id` | | ID обычного пользователя |

### Настройка коллекции

Создайте коллекцию `JavaTraining API`. Во вкладке **Authorization** выберите `Bearer Token` и укажите `{{token}}`.

Для автоматического сохранения токена после логина перейдите на вкладку **Scripts → Post-response** первого запроса `POST Login` и вставьте:

```javascript
var json = pm.response.json();
pm.collectionVariables.set("admin_token", json.token);
pm.collectionVariables.set("admin_id", json.userId);
```

Аналогично для регистрации USER — сохранить `user_token` и `user_id`.

### Запросы

| # | Метод | URL | Headers / Body | Ожидаемый результат |
|---|-------|-----|----------------|---------------------|
| 10.1 | **POST** | `{{base_url}}/api/auth/login` | **Headers:** `Content-Type: application/json`<br>**Body (raw JSON):** `{"username":"admin","password":"admin123"}`<br>**Tests (Post-response):** `pm.collectionVariables.set("admin_token", pm.response.json().token)` | **200** `{token, userId, username, role}` + refresh cookie в заголовках ответа |
| 10.2 | **POST** | `{{base_url}}/api/auth/login` | **Body:** `{"username":"admin","password":"wrong"}` | **401** |
| 10.3 | **POST** | `{{base_url}}/api/auth/register` | **Body:** `{"username":"apiuser","password":"pass123","email":"api@test.com"}`<br>**Tests:** `pm.collectionVariables.set("user_token", pm.response.json().token)` | **201** + AuthResponse (`token, userId, username, role`) |
| 10.4 | **GET** | `{{base_url}}/api/profile` | **Auth:** `Bearer {{admin_token}}` | **200** — `{id, username, email, nickname, about, avatarPath, role, createdAt}` |
| 10.5 | **GET** | `{{base_url}}/api/profile` | *(без токена)* → **Auth:** `No Auth` | **401** |
| 10.6 | **POST** | `{{base_url}}/api/profile` | **Headers:** `Content-Type: application/json`<br>**Auth:** `Bearer {{admin_token}}`<br>**Body:** `{"nickname":"ApiUser","about":"from api"}` | **200** — обновлённый профиль |
| 10.7 | **POST** | `{{base_url}}/api/profile/password` | **Headers:** `Content-Type: application/json`<br>**Auth:** `Bearer {{admin_token}}`<br>**Body:** `{"currentPassword":"admin123","newPassword":"newpass123","confirmPassword":"newpass123"}` | **200** |
| 10.8 | **GET** | `{{base_url}}/api/admin/users` | **Auth:** `Bearer {{admin_token}}` | **200** — массив UserDTO |
| 10.9 | **GET** | `{{base_url}}/api/admin/users` | **Auth:** `Bearer {{user_token}}` | **403** |
| 10.10 | **POST** | `{{base_url}}/api/admin/users/{{admin_id}}/block` | **Auth:** `Bearer {{admin_token}}` | **400** — `{"message":"Cannot block self"}` |
| 10.11 | **PUT** | `{{base_url}}/api/admin/users/{{admin_id}}/role` | **Headers:** `Content-Type: application/json`<br>**Auth:** `Bearer {{admin_token}}`<br>**Body:** `{"role":"USER"}` | **400** — `{"message":"You cannot change your own role"}` |
| 10.12 | **POST** | `{{base_url}}/api/test/start` | **Headers:** `Content-Type: application/json`<br>**Auth:** `Bearer {{user_token}}`<br>**Body:** `{"topics":["java-core"],"questionCount":5}` | **200** — первый вопрос + новая сессия (Set-Cookie) |
| 10.13 | **POST** | `{{base_url}}/api/test/question` | **Headers:** `Content-Type: application/json`<br>**Auth:** `Bearer {{user_token}}`<br>**Body:** `{"answerIndex":0}`<br>*(Cookie из ответа 10.12 должна быть отправлена)* | **204** |
| 10.14 | **GET** | `{{base_url}}/api/test/result` | **Auth:** `Bearer {{user_token}}`<br>*(Cookie из шага 10.12 должна быть отправлена)* | **200** — TestResultResponse (score, correct/total, passed) |
| 10.15 | **POST** | `{{base_url}}/api/auth/refresh` | *(Cookie `refreshToken` из ответа 10.1 отправляется автоматически)* | **200** — `{token, userId, username, role}` + новый refresh cookie |
| 10.16 | **POST** | `{{base_url}}/api/auth/refresh` | *(без refresh cookie)* | **400/401** |
| 10.17 | **POST** | `{{base_url}}/api/admin/topics/java-core/import` | **Auth:** `Bearer {{admin_token}}`<br>**Body:** `form-data`, поле `file` → выбрать .json файл | **200** — количество импортированных вопросов |
| 10.18 | **GET** | `{{base_url}}/api/admin/statistics` | **Auth:** `Bearer {{admin_token}}` | **200** — GlobalStatisticsResponse (totalTests, passedTests, userStats, topicStats) |

### Важные замечания для Postman

- **Токены:** Убедитесь, что в запросах 10.4–10.18 вкладка **Authorization** заполнена корректно (тип `Bearer Token` с переменной `{{admin_token}}` или `{{user_token}}`).
- **Cookies:** Запросы тестирования (10.12–10.14) требуют сессионную cookie. В Postman включите **"Automatically follow redirects" → OFF** и включите автоматическую отправку cookies в настройках коллекции.
- **Refresh:** Refresh token передаётся в httpOnly cookie, Postman отправляет её автоматически, если cookie была сохранена после login.
- **Переменные:** Используйте `pm.collectionVariables.set()` в скриптах **Post-response** для автоматического обновления токенов.

---

## Фаза 11 — Edge Cases

| # | Шаг | Ожидаемый результат |
|---|------|---------------------|
| 11.1 | Прямой доступ к `/admin/**` как USER (не через навбар) | 403 Forbidden |
| 11.2 | Удалить `_csrf` из POST-формы (через DevTools) | 403 Forbidden |
| 11.3 | Отправить запрос к `/api/profile` с подделанным/истёкшим JWT | 401 Unauthorized |
| 11.4 | Открыть `/my-stats` без тестов | Карточки = 0, кнопка "Пройти тест" |

---

## Фаза 12 — OAuth2 GitHub (если есть credentials)

| # | Шаг | Ожидаемый результат |
|---|------|---------------------|
| 12.1 | Нажать "Войти через GitHub" на `/login` | Редирект на GitHub OAuth |
| 12.2 | Подтвердить доступ | Редирект на `/`, user badge с GitHub-логином |
| 12.3 | Выйти, войти снова через GitHub | Тот же пользователь (по githubId) |
| 12.4 | `/profile` для GitHub-пользователя | Данные из GitHub (username, email), аватар по умолчанию |

---

## Чек-лист проверок после каждого теста

- [ ] Нет 500-х ошибок в консоли браузера
- [ ] Нет стека исключений в логах приложения
- [ ] Редиректы корректные (не остаётся на пустой странице)
- [ ] CSRF-токен присутствует во всех POST-формах (Thymeleaf)
- [ ] User badge отображает актуальные данные (nickname или username)
- [ ] Формы с enctype="multipart/form-data" имеют корректный accept

---

## Известные ограничения

1. **Нет валидации пароля на клиенте** — только серверная (>= 6 символов)
2. **Нет пагинации** в админ-таблицах — при большом числе записей страница может тормозить
3. **Refresh token** НЕ создаётся при формовой регистрации/входе — только через REST API (JWT Bearer). Cookie-путь `/api/auth/refresh` — не подходит для SPA на другом origin
4. **Максимальный размер загружаемого аватара** — 2 MB (application.yaml)
5. **Допустимые форматы аватара**: jpg, png, gif, svg, webp — проверка только по расширению файла
6. **Нет preview загруженного аватара** перед сохранением
7. **Нет REST-эндпоинта для смены аватара** через API — только через формы (MVC)
8. **Аватар при OAuth2** — не импортируется из GitHub, ставится дефолтный #1
