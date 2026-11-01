Сервер игры "Asteroids" на Kotlin. Принимает TCP-соединения от клиентов, управляет игровым процессом: регистрацией пользователей, созданием игр (комнат), обработкой команд кораблей (движение, сбор мусора) и рассылкой состояния игры.

## Стек

- Kotlin 2.1.10 (JVM)
- JUnit 5 (junit-jupiter-params, kotlin-test-junit5)
- Gradle 8.14 (Kotlin DSL), wrapper включён

## Конвенции
- Архитектура: Domain Driven Design
- Пакетная структура: `com.kiseru.asteroids.server.*`, интерфейсы и реализации разделяются (`handler` / `handler.impl`, `service` / `service.impl`).
- Тесты пишутся с JUnit 5 и кладутся в `src/test/kotlin/` зеркально пакету тестируемого класса.

# Команды
- Сборка проекта: gradlew build
- Запуск тестов: gradlew test
- Запуск сервера: gradlew run
