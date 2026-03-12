# Конструктор и движок процессов (MVP)

## Структура monorepo
- `backend/` — Spring Boot + Flowable API
- `frontend/` — React/Vite UI
- `docker-compose.yml` — запуск всего стека
- `.env.example` — переменные окружения

## Возможности MVP
- JWT регистрация/логин, роли USER/ADMIN
- CRUD процессов, версии BPMN XML
- Редактор BPMN в браузере через `bpmn-js`
- Публикация версии в Flowable (валидация + deploy)
- Запуск инстансов, просмотр инстансов/истории
- Мои задачи и completion с JSON variables
- Audit лог по процессам
- Admin endpoints для всех процессов/инстансов/задач
- Seed: `admin/admin`, `demo/demo`, demo процесс **Домашние дела**

## Быстрый запуск
```bash
cp .env.example .env
docker compose up --build
```

Приложения:
- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- OpenAPI: http://localhost:8080/swagger-ui.html

## Основные API
- Auth: `/api/auth/register`, `/api/auth/login`, `/api/auth/me`
- Processes: `/api/processes/**`
- Instances: `/api/instances/**`
- Tasks: `/api/tasks/**`
- Admin: `/api/admin/**`

## Примечания
- Продуктовые таблицы отдельны от таблиц Flowable.
- Для user tasks в demo BPMN используется assignee `${assignee}`; при старте можно передать `assigneeGroupId` в теле запроса, тогда сервис автоматически запишет его в process variable `assignee`.
- `GET /api/tasks/my` возвращает личные задачи пользователя и задачи групп, в которых он состоит. Поддерживается необязательный параметр `groupType` (код типа группы) для фильтрации групповых задач.
- Для процессов с `category=game`: каждая `userTask` считается уровнем. Коды уровня задаются на уровне процесса через `PUT /api/processes/{id}/levels/{levelKey}/codes` и одинаковы для всех инстансов. В `POST /api/tasks/{taskId}/complete` можно передавать поле `code`; задача завершится автоматически, когда введены все коды уровня. Также доступен отдельный endpoint ввода кода `POST /api/game/tasks/{taskId}/codes`.


## Backend (Gradle)
```bash
cd backend
./gradlew bootRun
```
