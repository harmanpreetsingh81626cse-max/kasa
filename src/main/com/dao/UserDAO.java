package main.com.dao;

import main.com.config.*;
import main.com.models.*;
import main.com.enums.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserDAO {
    public int saveUser(User user) throws SQLException {
        String sql = """
                INSERT INTO users
                (user_name, email, password, phone, address,
                 role, identity_verified)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getPhone());

            if (user instanceof Buyer) {
                Buyer buyer = (Buyer) user;
                pstmt.setString(5, buyer.getAddress());
            } else {
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            }

            pstmt.setString(6, user.getRole().name());

            if (user instanceof Owner) {
                Owner owner = (Owner) user;
                pstmt.setBoolean(7, owner.isIdentityVerified());
            } else {
                pstmt.setBoolean(7, false);
            }

            int rows = pstmt.executeUpdate();

            if (rows == 0) {
                throw new SQLException("User insertion failed.");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("User ID was not generated.");
            }
        }
    }

    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
                return Optional.empty();
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {

        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password");
        String phone = rs.getString("phone");

        Timestamp timestamp = rs.getTimestamp("created_at");

        LocalDateTime createdAt = timestamp != null
                ? timestamp.toLocalDateTime()
                : null;

        UserRole role = UserRole.valueOf(rs.getString("role"));

        switch (role) {

            case BUYER:
                return new Buyer(
                        userId,
                        username,
                        email,
                        passwordHash,
                        phone,
                        createdAt,
                        rs.getString("address"));

            case OWNER:
                return new Owner(
                        userId,
                        username,
                        email,
                        passwordHash,
                        phone,
                        createdAt,
                        rs.getBoolean("identity_verified"));

            case ADMIN:
                return new Admin(
                        userId,
                        username,
                        email,
                        passwordHash,
                        phone,
                        createdAt);

            default:
                throw new SQLException("Invalid user role.");
        }
    }

}
