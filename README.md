# 🏦 Banking App — консольное банковское приложение на Java

Консольное Java-приложение для управления банковскими счетами и переводами между ними. Поддерживает три типа счетов с разными правилами комиссии и дневных лимитов, хранит данные в PostgreSQL через JDBC, а при недоступности базы переключается в оффлайн-режим (в памяти).

## Возможности

- Создание счетов трёх типов: **Personal**, **Business**, **VIP**
- Просмотр списка счетов с балансами
- Переводы между счетами с проверкой баланса, дневного лимита и расчётом комиссии
- Журнал транзакций (тип, сумма, комиссия, время, отправитель/получатель)
- Безопасные конкурентные переводы за счёт построчной блокировки (`SELECT ... FOR UPDATE`) и транзакций БД
- Оффлайн-режим: если PostgreSQL недоступен, приложение работает с данными в памяти

## Типы счетов

| Тип | Комиссия за перевод | Дневной лимит |
|---|---|---|
| **Personal** | 0% | 100 000 ₽ |
| **Business** | 2% от суммы | 1 000 000 ₽ |
| **VIP** | max(1₽, 0.5% от суммы) | без ограничений |

## Архитектура проекта

```
src/main/java/
├── models/        # доменные сущности
│   ├── Account.java          — абстрактный базовый класс счёта
│   ├── PersonalAccount.java
│   ├── BusinessAccount.java
│   ├── VipAccount.java       — реализации с разными правилами комиссии/лимита
│   ├── AccountFactory.java   — фабрика для создания счёта по типу
│   ├── AccountType.java      — enum типов счетов
│   ├── Transaction.java      — запись об операции
│   └── TransactionType.java  — enum типов операций (TRANSFER/DEPOSIT/WITHDRAW)
├── dao/           # доступ к данным (JDBC)
│   ├── AccountDAO.java       — CRUD по счетам, поиск с блокировкой строки
│   └── TransactionDAO.java   — запись истории транзакций
├── services/      # бизнес-логика переводов
│   ├── TransactionService.java    — переводы в оффлайн-режиме (in-memory)
│   └── TransactionServiceDb.java  — переводы с БД-транзакцией и rollback при ошибке
├── db/
│   └── DatabaseConnection.java — подключение к PostgreSQL
├── ui/
│   └── SafeInput.java          — безопасный ввод данных из консоли (валидация чисел, сумм, типов)
└── org/example/Main.java        — точка входа, CLI-меню
```

## CLI-меню

```
=== Bank CLI ===
1) Показать счета
2) Создать счёт
3) Перевод
0) Выход
```

## Запуск

### Требования

- Java 17+ (проект собирается с таргетом Java 24)
- Maven 3.8+
- PostgreSQL 15+ (опционально — без него приложение работает в оффлайн-режиме)

### С PostgreSQL

1. Создать базу данных и применить схему:
   ```bash
   createdb bank_db
   psql -U postgres -d bank_db -f src/main/resources/schema.sql
   ```
2. Указать параметры подключения (хост, порт, имя БД, логин, пароль) в `src/main/java/db/DatabaseConnection.java`.
3. Собрать и запустить:
   ```bash
   mvn clean package
   mvn exec:java -Dexec.mainClass=org.example.Main
   ```

### Без PostgreSQL (оффлайн-режим)

Если подключение к базе недоступно, приложение само переключится в оффлайн-режим — счета и переводы хранятся в памяти на время сессии.

## Схема базы данных

```sql
accounts (id, owner_name, balance, account_type)
transactions (id, created_at, type, amount, fee, from_account_id, to_account_id)
```

Полная схема — в [`src/main/resources/schema.sql`](src/main/resources/schema.sql).

## Стек

- **Java** (JDK 24)
- **Maven**
- **PostgreSQL** + JDBC (`org.postgresql:postgresql`)
- Паттерны: **Factory**, абстрактные классы для полиморфного поведения счетов, **DAO**
