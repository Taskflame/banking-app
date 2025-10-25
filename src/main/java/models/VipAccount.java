package models;

public class VipAccount extends Account {

    public VipAccount(int id, String ownerName, double balance) {
        super(id, ownerName, balance);
    }

    @Override
    public double getTransactionFee(double amount){
        return Math.max(1, amount * 0.005);
    }

    @Override
    public double getDailyLimit() {
        return Double.MAX_VALUE;
    }
}
