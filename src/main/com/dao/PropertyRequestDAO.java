package main.com.dao;

import main.com.config.DatabaseConnection;
import main.com.models.PropertyRequest;
import main.com.enums.RequestStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PropertyRequestDAO {

    public int addRequest(PropertyRequest request) throws SQLException {

        String sql = """
                INSERT INTO property_requests
                (property_id, buyer_id, message, status)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, request.getPropertyId());
            ps.setInt(2, request.getBuyerId());
            ps.setString(3, request.getMessage());
            ps.setString(4, request.getStatus().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    public Optional<PropertyRequest> findById(int requestId)
            throws SQLException {

        String sql = """
                SELECT * FROM property_requests
                WHERE request_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapRequest(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<PropertyRequest> findByBuyerId(int buyerId)
            throws SQLException {

        List<PropertyRequest> requests = new ArrayList<>();

        String sql = """
                SELECT * FROM property_requests
                WHERE buyer_id = ?
                ORDER BY created_at DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, buyerId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    requests.add(mapRequest(rs));
                }
            }
        }

        return requests;
    }

    public List<PropertyRequest> findByOwnerId(int ownerId)
            throws SQLException {

        List<PropertyRequest> requests = new ArrayList<>();

        String sql = """
                SELECT pr.*
                FROM property_requests pr
                JOIN properties p ON pr.property_id = p.property_id
                WHERE p.owner_id = ?
                ORDER BY pr.created_at DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    requests.add(mapRequest(rs));
                }
            }
        }

        return requests;
    }

    public boolean updateStatus(int requestId, RequestStatus status)
            throws SQLException {

        String sql = """
                UPDATE property_requests
                SET status = ?
                WHERE request_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, requestId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteRequest(int requestId, int buyerId)
            throws SQLException {

        String sql = """
                DELETE FROM property_requests
                WHERE request_id = ? AND buyer_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ps.setInt(2, buyerId);

            return ps.executeUpdate() > 0;
        }
    }

    private PropertyRequest mapRequest(ResultSet rs)
            throws SQLException {

        Timestamp timestamp = rs.getTimestamp("created_at");

        LocalDateTime createdAt = timestamp != null
                ? timestamp.toLocalDateTime()
                : null;

        return new PropertyRequest(
                rs.getInt("request_id"),
                rs.getInt("property_id"),
                rs.getInt("buyer_id"),
                rs.getString("message"),
                RequestStatus.valueOf(rs.getString("status")),
                createdAt);
    }
}