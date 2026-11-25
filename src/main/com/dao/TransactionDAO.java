package main.com.dao;

import main.com.config.DatabaseConnection;
import main.com.models.Transaction;
import main.com.enums.TransactionStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class TransactionDAO {

    // 1. ADD TRANSACTION
    public int addTransaction(Transaction transaction) throws SQLException {

        String sql = """
                INSERT INTO transactions
                (property_id, buyer_id, seller_id, amount, status)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, transaction.getPropertyId());
            ps.setInt(2, transaction.getBuyerId());
            ps.setInt(3, transaction.getSellerId());
            ps.setBigDecimal(4, transaction.getAmount());
            ps.setString(5, transaction.getStatus().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    public Optional<Transaction> findById(int transactionId)
            throws SQLException {

        String sql = """
                SELECT * FROM transactions
                WHERE transaction_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, transactionId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapTransaction(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<Transaction> findByBuyerId(int buyerId)
            throws SQLException {

        List<Transaction> transactions = new ArrayList<>();

        String sql = """
                SELECT * FROM transactions
                WHERE buyer_id = ?
                ORDER BY transaction_date DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, buyerId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    transactions.add(mapTransaction(rs));
                }
            }
        }

        return transactions;
    }

    public List<Transaction> findBySellerId(int sellerId)
            throws SQLException {

        List<Transaction> transactions = new ArrayList<>();

        String sql = """
                SELECT * FROM transactions
                WHERE seller_id = ?
                ORDER BY transaction_date DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sellerId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    transactions.add(mapTransaction(rs));
                }
            }
        }

        return transactions;
    }

    public boolean updateStatus(int transactionId, TransactionStatus status)
            throws SQLException {

        String sql = """
                UPDATE transactions
                SET status = ?
                WHERE transaction_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, transactionId);

            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Transaction> findByPropertyId(int propertyId)
            throws SQLException {

        String sql = """
                SELECT * FROM transactions
                WHERE property_id = ?
                ORDER BY transaction_date DESC
                LIMIT 1
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, propertyId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapTransaction(rs));
                }
            }
        }

        return Optional.empty();
    }

    private Transaction mapTransaction(ResultSet rs) throws SQLException {

        Timestamp timestamp = rs.getTimestamp("transaction_date");

        LocalDateTime transactionDate = timestamp != null
                ? timestamp.toLocalDateTime()
                : null;

        return new Transaction(
                rs.getInt("transaction_id"),
                rs.getInt("property_id"),
                rs.getInt("buyer_id"),
                rs.getInt("seller_id"),
                rs.getBigDecimal("amount"),
                TransactionStatus.valueOf(rs.getString("status")),
                transactionDate);
    }
}