package models;

import java.time.LocalDateTime;

public class Transaction {
    private static int counter = 1;

    private final int id;                  // уникальный номер транзакции
    private final LocalDateTime timestamp; // время, когда транзакция была создана
    private final TransactionType type;    // тип операции
    private final double amount;           // сумма перевода
    private final double fee;              // комиссия при переводе
    private final int fromId;
    private final int toId;

    public Transaction(TransactionType type, double amount, double fee, int fromId, int toId){
        this.id = counter++;
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.fee = fee;
        this.fromId = fromId;
        this.toId = toId;
    }

    public int getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public TransactionType getType() { return  type; }
    public double getAmount() { return amount; }
    public double getFee() { return fee; }
    public int getFromId() { return fromId; }
    public int getToId() { return toId; }

    @Override
    public String toString() {
        return String.format(
                "Транзакция #%d [%s] %.2f₽ (комиссия %.2f₽) от %d к %d [%s]",
                id,
                type,
                amount,
                fee,
                fromId,
                toId,
                timestamp
        );
    }

}
