package dao;

import models.Transaction;
import models.TransactionType;

import java.sql.*;

public class TransactionDAO {
    public void insert(Connection conn, Transaction tx) throws SQLException {
        String sql =
                "INSERT INTO transactions (type, amount, fee, from_account_id, to_account_id) " +
                        "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tx.getType().name());
            ps.setBigDecimal(2, java.math.BigDecimal.valueOf(tx.getAmount()));
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(tx.getFee()));
            ps.setInt(4, tx.getFromId());
            ps.setInt(5, tx.getToId());
            ps.executeUpdate();
        }
    }
}

