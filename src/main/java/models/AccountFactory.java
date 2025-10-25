package models;

public final class AccountFactory {
    private AccountFactory(){}

    public static Account create(int id, String owner, double balance, AccountType type) {
        switch (type) {
            case PERSONAL: return new PersonalAccount(id, owner, balance);
            case BUSINESS: return new BusinessAccount(id, owner, balance);
            case VIP: return new VipAccount(id, owner, balance);
            default: throw new IllegalArgumentException("Unknown type: " + type);

        }
    }

}
