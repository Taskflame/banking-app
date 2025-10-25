# 🏦 Java Banking Application — Подключение к PostgreSQL

Этот проект использует **PostgreSQL** для хранения данных (счета и транзакции).  
Ниже описано, как любой пользователь GitHub может запустить проект **со своей базой данных** на компьютере.

---

## 📋 Что потребуется

| Компонент | Версия | Назначение |
|------------|---------|-------------|
| **Java JDK** | 17+ | запуск приложения |
| **PostgreSQL** | 15+ | база данных |
| **Maven** | 3.8+ | сборка проекта и зависимости |
| **IntelliJ IDEA** *(или VS Code)* | — | среда для запуска |

---

## ⚙️ 1. Клонирование репозитория

1) Сначала скачайте проект к себе:
```bash
git clone https://github.com/Taskflame/banking-app.git
cd banking-app
```
2) Установка PostgreSQL 
```
💻 Windows
Скачайте установщик с официального сайта PostgreSQL.

Установите и запомните:
логин (по умолчанию — postgres)
пароль, который вы зададите во время установки

После установки запустите SQL Shell (psql).
```

3) Создание базы данных

```
После установки PostgreSQL нужно создать базу, в которой приложение будет хранить данные.

Откройте терминал или psql и введите:
CREATE DATABASE bank_db;

Проверьте, что база создалась:
\l
```

4) Создание таблиц
```commandline
В проекте уже есть SQL-скрипт schema.sql который создаёт нужные таблицы.

Запустите его командой:
psql -U postgres -d bank_db -f src/main/resources/schema.sql

После этого появятся таблицы:
accounts хранит информацию о счетах
transactions хранит историю переводов
```

5) Настройка подключения в коде
```commandline
Теперь нужно указать данные для подключения к базе данных в файле:
src/main/java/db/DatabaseConnection.java

Найдите эти строки и замените на свои:
private static final String URL = "jdbc:postgresql://localhost:5432/bank_db";
private static final String USER = "postgres";          // ваш логин
private static final String PASSWORD = "your_password"; // ваш пароль

Если вы используете другой порт (например, 5433), измените число после localhost:.
```
