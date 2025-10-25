package org.example;

import dao.AccountDAO;
import db.DatabaseConnection;
import models.*;
import services.TransactionService;
import services.TransactionServiceDb;
import ui.SafeInput;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final TransactionServiceDb txServiceDb = new TransactionServiceDb();
    private static final TransactionService txServiceOffline = new TransactionService();
    private static final AccountDAO accountDAO = new AccountDAO();

    // локальное хранилище счетов для оффлайн-режима
    private static final List<Account> localAccounts = new ArrayList<>();

    public static void main(String[] args) {
        boolean dbAvailable = testDatabaseConnection();
        System.out.println(dbAvailable
                ? "Режим: работа с PostgreSQL"
                : "Режим: оффлайн (данные не сохраняются в БД)");

        while (true) {
            System.out.println("\n=== Bank CLI ===");
            System.out.println("1) Показать счета");
            System.out.println("2) Создать счёт");
            System.out.println("3) Перевод");
            System.out.println("0) Выход");

            int cmd = SafeInput.readInt("Выберите пункт: ");
            try {
                switch (cmd) {
                    case 1 -> showAccounts(dbAvailable);
                    case 2 -> createAccount(dbAvailable);
                    case 3 -> doTransfer(dbAvailable);
                    case 0 -> {
                        System.out.println("Пока!");
                        return;
                    }
                    default -> System.out.println("Нет такого пункта.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    // проверяем доступность БД
    private static boolean testDatabaseConnection() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return conn != null && conn.isValid(1);
        } catch (Exception e) {
            return false;
        }
    }

    private static void showAccounts(boolean dbAvailable) throws Exception {
        List<Account> list = dbAvailable ? accountDAO.findALL() : localAccounts;
        if (list.isEmpty()) {
            System.out.println("(пока пусто)");
            return;
        }
        System.out.println("\nID | Владелец            | Баланс    | Тип");
        for (Account a : list) {
            System.out.printf("%2d | %-20s | %9.2f | %s%n",
                    a.getId(), a.getOwnerName(), a.getBalance(),
                    a.getClass().getSimpleName().replace("Account", "").toLowerCase());
        }
    }

    private static void createAccount(boolean dbAvailable) throws Exception {
        String owner = SafeInput.readNonEmpty("Имя владельца: ");
        double balance = SafeInput.readPositiveMoney("Начальный баланс: ");
        AccountType type = SafeInput.readAccountType("Тип клиента");

        if (dbAvailable) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);
                Account created = accountDAO.create(conn, owner, balance, type);
                conn.commit();
                System.out.printf("Создан счёт (БД): id=%d, %s, баланс=%.2f, тип=%s%n",
                        created.getId(), created.getOwnerName(), created.getBalance(), type);
            }
        } else {
            int newId = localAccounts.size() + 1;
            Account created = AccountFactory.create(newId, owner, balance, type);
            localAccounts.add(created);
            System.out.printf("Создан счёт (offline): id=%d, %s, баланс=%.2f, тип=%s%n",
                    created.getId(), created.getOwnerName(), created.getBalance(), type);
        }
    }

    private static void doTransfer(boolean dbAvailable) throws Exception {
        int fromId = SafeInput.readInt("Со счёта (id): ");
        int toId = SafeInput.readInt("На счёт (id): ");
        double amount = SafeInput.readPositiveMoney("Сумма перевода: ");

        if (dbAvailable) {
            txServiceDb.transfer(fromId, toId, amount);
        } else {
            Account from = findLocalAccount(fromId);
            Account to = findLocalAccount(toId);
            if (from == null || to == null) throw new IllegalArgumentException("Счёт не найден.");
            txServiceOffline.transfer(from, to, amount);
        }
    }

    private static Account findLocalAccount(int id) {
        for (Account a : localAccounts) {
            if (a.getId() == id) return a;
        }
        return null;
    }
}

