Проект: Інтелектуальна система виявлення аномалій мережевого трафіку
Студент: Євлампієв Владислав Юрійович, група 405

НЕОБХІДНЕ ПЗ:
1. Java 21
2. PostgreSQL (порт 5432)
3. Docker (для RabbitMQ)
4. Postman

ПІДГОТОВКА ДО ЗАПУСКУ:
1. База даних:
   Створіть у PostgreSQL дві порожні бази даних:
   - traffic_auth_db
   - traffic_data_db
   (Таблиці створяться автоматично через Flyway при запуску).

2. RabbitMQ:
   Запустіть контейнер командою:
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

3. Налаштування:
   Перевірте файли application.yml у кожному сервісі.
   Встановіть свої username/password для PostgreSQL, якщо вони відрізняються від 'postgres/password'.

ПОРЯДОК ЗАПУСКУ:
1. EurekaServerApplication
2. AuthServiceApplication
3. TrafficAnomalyApplication
4. NotificationApplication

ТЕСТУВАННЯ:
1. Відкрийте браузер: http://localhost:8083/index.html (для моніторингу Websocket).
2. Імпортуйте колекцію Postman (якщо є) або зробіть запит GET http://localhost:8082/api/traffic/analyze (з токеном).