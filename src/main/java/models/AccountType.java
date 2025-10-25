package models;

public enum AccountType {
    PERSONAL("personal"),
    BUSINESS("business"),
    VIP("vip");

    private final String dbValue;

    AccountType (String dbValue) {
        this.dbValue = dbValue;
    }
    public String db() { return dbValue; }

    public static AccountType fromDb(String v) {
        switch (v) {
            case "personal": return PERSONAL;
            case "business": return BUSINESS;
            case "vip": return VIP;
        }
        throw new IllegalArgumentException("Unknown account_type: " + v);

    }

}
