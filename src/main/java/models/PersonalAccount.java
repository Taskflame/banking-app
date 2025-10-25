package models;

public class PersonalAccount extends Account {

    public PersonalAccount(int id, String ownerName, double balance) {
        super(id, ownerName, balance);
    }

    @Override
    public double getTransactionFee(double amount){ // коммиссия за месяц для персонального клиента
        return 0; // не берём
    }

    @Override
    public double getDailyLimit() {
        return 100_000; // ограничение на перевод в сутки
    }
}
