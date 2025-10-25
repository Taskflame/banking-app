package org.example;

import dao.AccountDAO;
import db.DatabaseConnection;
import models.Account;
import models.AccountType;
import services.TransactionServiceDb;
import ui.SafeInput;
import models.*;

import java.sql.Connection;
import java.util.List;

public class Main {
    private static final TransactionServiceDb txService = new TransactionServiceDb();
    private static final AccountDAO accountDAO = new AccountDAO();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Bank CLI ===");
            System.out.println("1) Показать счета");
            System.out.println("2) Создать счёт");
            System.out.println("3) Перевод");
            System.out.println("0) Выход");

            int cmd = SafeInput.readInt("Выберите пункт: ");
            try {
                switch (cmd) {
                    case 1:
                        showAccounts();
                        break;
                    case 2:
                        createAccount();
                        break;
                    case 3:
                        doTransfer();
                        break;
                    case 0:
                        System.out.println("Пока!");
                        return;
                    default:
                        System.out.println("Нет такого пункта.");
                }
            } catch (Exception e) {
                // здесь ловим любые ошибки на операции и выводим дружелюбное сообщение
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static void showAccounts() throws Exception {
        List<Account> list = accountDAO.findALL();
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

    private static void createAccount() throws Exception {
        String owner = SafeInput.readNonEmpty("Имя владельца: ");
        double balance = SafeInput.readPositiveMoney("Начальный баланс: ");
        AccountType type = SafeInput.readAccountType("Тип клиента");

        // лучше создавать в явной транзакции (на будущее)
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            Account created = accountDAO.create(conn, owner, balance, type);
            conn.commit();
            System.out.printf("Создан счёт: id=%d, %s, баланс=%.2f, тип=%s%n", created.getId(), created.getOwnerName(), created.getBalance(), type);
        }
    }

    private static void doTransfer() throws Exception {
        int fromId = SafeInput.readInt("Со счёта (id): ");
        int toId   = SafeInput.readInt("На счёт (id): ");
        double amount = SafeInput.readPositiveMoney("Сумма перевода: ");
        txService.transfer(fromId, toId, amount);
        System.out.println("Перевод выполнен.");
    }
}
