 # Neoflex Bank Project

## Описание проекта

На данном проекте будет реализовано бэкенд-приложение с микросервисной архитектурой - прототип небольшого банка. 

Будут преобретены знания и навыки, необходимые для любого современного бэкенд- разработчика:

- Разработка REST-API с использованием Spring Boot;

- Работа с базой данных, с использованием инструментов миграции;

- Знание микросервисной архитектуры;

- Синхронное и асинхронное взаимодействие сервисов через брокеры сообщений;

- Документирование API через Swagger/OpenAPI;

- Настройка CI-пайплайнов, контейнеризация приложения.

## Логика работы всей системы

1. Пользователь отправляет заявку на кредит.
2. МС Заявка осуществляет прескоринг прескорингзаявки и если прескоринг проходит, то заявка сохраняется в МС Сделка и отправляется в МС калькулятор.
3. МС Калькулятор возвращает через МС Заявку пользователю 4 предложения (сущность "LoanOffer") по кредиту с разными условиями (например без страховки, со страховкой, с зарплатным клиентом, со страховкой и зарплатным клиентом) или отказ.
4. Пользователь выбирает одно из предложений, отправляется запрос в МС Заявка, а оттуда в МС Сделка, где заявка на кредит и сам кредит сохраняются в базу.
5. МС Досье отправляет клиенту письмо с текстом "Ваша заявка предварительно одобрена, завершите оформление".
6. Клиент отправляет запрос в МС Сделка со всеми своими полными данными о работодателе и прописке.
Происходит скоринг данных в МС Калькулятор, МС Калькулятор рассчитывает все данные по кредиту (ПСК, график платежей и тд), МС Сделка сохраняет обновленную заявку и сущность кредит сделанную на основе CreditDto полученного из КК со статусом CALCULATED в БД.
7. После валидации МС Досье отправляет письмо на почту клиенту с одобрением или отказом.
Если кредит одобрен, то в письме присутствует ссылка на запрос "Сформировать документы"
8. Клиент отправляет запрос на формирование документов в МС Досье, МС Досье отправляет клиенту на почту документы для подписания и ссылку на запрос на согласие с условиями.
9. Клиент может отказаться от условий или согласиться.
Если согласился - МС Досье на почту отправляет код и ссылку на подписание документов, куда клиент должен отправить полученный код в МС Сделка.
10. Если полученный код совпадает с отправленным, МС Сделка выдает кредит (меняет статус сущности "Кредит" на ISSUED, а статус заявки на CREDIT_ISSUED)


## API
### 9.1. calculator
POST: /calculator/offers - расчёт возможных условий кредита.
POST: /calculator/calc - валидация присланных данных + полный расчет параметров кредита 
### 9.2. deal
POST: /deal/statement - расчёт возможных условий кредита
POST: /deal/offer/select - выбор одного из предложений
POST: /deal/calculate/{statementId} -  полный расчет параметров кредита 
POST: /deal/document/{statementId}/send - запрос на отправку документов
POST: /deal/document/{statementId}/sign - запрос на подписание документов
POST: /deal/document/{statementId}/code - подписание документов
GET: /deal/admin/statement/{statementId} - получить заявку по id 
PUT: /deal/admin/statement/{statementId}/status - обновить статус заявки
### 9.3. statement
POST: /statement - первичная валидация заявки, создание заявки
POST: /statement/offer - выбор одного из предложений

## MVP Level 1 Реализация микросервиса Калькулятор (calculator)

### POST: /calculator/offers

По API приходит LoanStatementRequestDto.
На основании LoanStatementRequestDto происходит прескоринг, создаётся 4 кредитных предложения LoanOfferDto на основании всех возможных комбинаций булевских полей isInsuranceEnabled и isSalaryClient (false-false, false-true, true-false, true-true).
Логику формирования кредитных предложений можно придумать самому.
Ответ на API - список из 4х LoanOfferDto от "худшего" к "лучшему" (чем меньше итоговая ставка, тем лучше).

### POST: /calculator/calc

По API приходит ScoringDataDto.
Происходит скоринг данных, высчитывание итоговой ставки (rate), полной стоимости кредита (psk), размера ежемесячного платежа (monthlyPayment), график ежемесячных платежей (List<PaymentScheduleElementDto>).
Ответ на API - CreditDto, насыщенный всеми рассчитанными параметрами.


### Docker Compose
Для запуска в корневой директории выполните команду 
```sh
docker-compose up --build
