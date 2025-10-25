package services;

import models.*;

public class TransactionService {

    public void transfer(Account from, Account to, double amount){
        System.out.println("\n️Начинаем перевод");
        // подсчёт комиссии и итога списания
        double fee = from.getTransactionFee(amount);
        double total = amount + fee;

        if (amount <= 0){
            throw new IllegalArgumentException("Сумма должна быть положительной");
        }

        if (from.getBalance() < total){
            throw new IllegalArgumentException("Недостаточно средств на счёте");
        }

        if (amount > from.getDailyLimit()) {
            throw new IllegalStateException("Превышен дневной лимит перевода");
        }
        // движение денег в памяти
        from.setBalance(from.getBalance() - total);
        to.setBalance(to.getBalance() + amount);

        Transaction tx = new Transaction(
                TransactionType.TRANSFER,
                amount,
                fee,
                from.getId(),
                to.getId()
        );

        from.addTransaction(tx);
        to.addTransaction(tx);

        System.out.printf(
                "Перевод %.2f₽ завершён: %s ➡ %s (комиссия %.2f₽)%n",
                amount, from.getOwnerName(), to.getOwnerName(), fee
        );

    }
}

// уменьшить сумму до
// добавить SQL
