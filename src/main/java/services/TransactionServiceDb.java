package services;

import dao.AccountDAO;
import dao.TransactionDAO;
import db.DatabaseConnection;
import models.*;

import java.sql.Connection;

public class TransactionServiceDb {
    // используем два созданных нами файла для связи с базой данных
    private final AccountDAO accountDAO = new AccountDAO(); // загрузка/обновление аккаунтов
    private final TransactionDAO transactionDAO = new TransactionDAO(); // загрузка/обновление аккаунтов

    public void transfer(int fromId, int toId, double amount) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); //
            try {
                Account from = accountDAO.findByIdForUpdate(conn, fromId); // делаем так, чтобы две параллельные опреации не могли менять одновременно один и тот же баланс
                Account to = accountDAO.findByIdForUpdate(conn, toId);
                if (from == null || to == null) throw new IllegalArgumentException("Счёт не найден");
                double fee = from.getTransactionFee(amount);
                double total = amount + fee;

                if (amount <= 0) throw new IllegalArgumentException("Сумма должна быть положительной");
                if (from.getBalance() < total) throw new IllegalArgumentException("Недостаточно средств");
                if (amount > from.getDailyLimit()) throw new IllegalArgumentException("Превышен дневной лимит");

                // Обновляем балансы в памяти
                from.setBalance(from.getBalance() - total);
                to.setBalance(to.getBalance() + amount);

                // Пишем новые балансы в БД
                accountDAO.updateBalance(conn, from.getId(), from.getBalance());
                accountDAO.updateBalance(conn, to.getId(), to.getBalance());

                Transaction tx = new Transaction(
                        TransactionType.TRANSFER, amount, fee, from.getId(), to.getId());
                transactionDAO.insert(conn, tx);

                conn.commit();
                System.out.printf("Перевод %.2f₽: %s ➡ %s (комиссия %.2f₽)%n", amount, from.getOwnerName(), to.getOwnerName(), fee);

            } catch (Exception e) {
                conn.rollback();
                throw e;

            }
        }
    }
}
