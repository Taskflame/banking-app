package dao;

import db.DatabaseConnection;
import models.*;
import java.sql.*;

public class AccountDAO { // в этом классе находятся методы для чтения/записи данных в таблицу

    public Account findById(int id) throws SQLException {
        // SQL с плейсхолдером "?" — реальное значение подставим ниже
        String sql = "SELECT id, owner_name, balance, account_type FROM accounts WHERE id = ?";
        // Открываем Connection (через DatabaseConnection.getConnection()).
        // try-with-resources гарантирует закрытие conn и ps после выхода из блока (даже при ошибке).
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            // Привязываем 1-й параметр (вместо "?") — сюда пойдёт наше значение id.
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return AccountFactory.create(
                        rs.getInt("id"),
                        rs.getString("owner_name"),
                        rs.getDouble("balance"),
                        AccountType.fromDb(rs.getString("account_type"))

                );
            }
        }
    }
    // поиск с блокировкой. Нужен для того, чтобы другие транзакции не могли делать изменения в счёте, пока не закончиться предыдущая транзакция.
    public Account findByIdForUpdate(Connection conn, int id) throws SQLException {

        String sql = "SELECT id, owner_name, balance, account_type FROM accounts WHERE id = ? FOR UPDATE"; // блокировка строки до COMMIT/ROLLBACK

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return AccountFactory.create(
                        rs.getInt("id"),
                        rs.getString("owner_name"),
                        rs.getDouble("balance"),
                        AccountType.fromDb(rs.getString("account_type"))
                );
            }
        }
    }

    public void updateBalance(Connection conn, int accountId, double newBalance) throws SQLException {

        String sql = "UPDATE accounts SET balance = ? WHERE ID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, java.math.BigDecimal.valueOf(newBalance));
            ps.setInt(2, accountId);
            int updated = ps.executeUpdate();
            if (updated != 1) {
                throw new SQLException("Balance update failed for id=" + accountId + ", updated=" + updated);
            }
        }
    }

    //  создать аккаунт
    public Account create(Connection conn, String owner, double balance, AccountType type) throws SQLException {
        String sql = "INSERT INTO accounts(owner_name, balance, account_type) VALUES (?,?,?) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, owner);
            ps.setBigDecimal(2, java.math.BigDecimal.valueOf(balance));
            ps.setString(3, type.db());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("INSERT account не вернул id");
                int id = rs.getInt(1);
                return AccountFactory.create(id, owner, balance, type);
            }
        }
    }

    // список всех аккаунтов
    public java.util.List<Account> findALL() throws SQLException {
        String sql = "SELECT id, owner_name, balance, account_type FROM accounts ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            java.util.List<Account> out = new java.util.ArrayList<>();
            while (rs.next()) {
                out.add(AccountFactory.create(
                                rs.getInt("id"),
                                rs.getString("owner_name"),
                                rs.getDouble("balance"),
                                AccountType.fromDb(rs.getString("account_type"))
                        )
                );
            }
            return out;
        }
    }
}


