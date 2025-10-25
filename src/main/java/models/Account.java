package models;

import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    protected int id;
    protected String ownerName;
    protected double balance;
    protected List<Transaction> transactions;                  // журнал операций

    public Account(int id, String ownerName, double balance) { // создаю конструктор базового класса
        this.id = id;
        this.ownerName = ownerName;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getOwnerName() { return ownerName; }
    public double getBalance() { return balance; }

    public void setBalance(double balance){
        this.balance = balance;
    }

    public abstract double getTransactionFee(double amount); // абстрактный метод без тела, который говорит, что все наследники обязаны реализовать его по-своему
    public abstract double getDailyLimit();

    public void addTransaction(Transaction t){
        transactions.add(t);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @Override                                                 // говорим компилятору, что метод наже должен переопределять метод из стандартного интерфейса
    public String toString() {
        return String.format(
        "Account{id=%d, owner='%s', balance=%.2f}",
                id,
                ownerName,
                balance
        );
    }
}
