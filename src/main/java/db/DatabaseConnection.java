package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection { //открытый класс, чтобы можно было вызывать из других пакетов
    private static final String URL = "jdbc:postgresql://localhost:5432/bank_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "konstanta981";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD); // создаём соединение по URL, USER, PASSWORD
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Подключение к базе успешно!");
        } catch (SQLException e) {
            System.out.println("Ошибка подключения: " + e.getMessage());
        }
    }
}
