package models;

public class BusinessAccount extends Account {

    private static final double TRANSACTION_FEE_RATE = 0.02;

    public BusinessAccount(int id, String ownerName, double balance){
        super(id, ownerName, balance);
    }

    @Override
    public double getTransactionFee(double amount) { // месячная плата для бизнеса
        return amount * TRANSACTION_FEE_RATE;              // счиатем 2% от текущего баланса
    }

    @Override
    public double getDailyLimit() {
        return 1_000_000;
    }
}
