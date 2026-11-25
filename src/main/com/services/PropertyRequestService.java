package main.com.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import main.com.config.DatabaseConnection;
import main.com.enums.RequestStatus;
import main.com.exceptions.PropertyNotFoundException;
import main.com.exceptions.PropertyUnavailableException;
import main.com.exceptions.UnauthorizedOperationException;
import main.com.models.Property;
import main.com.models.PropertyRequest;

public class PropertyRequestService {

    private final PropertyService propertyService = new PropertyService();

    public int createRequest(int buyerId, int propertyId, String message)
            throws SQLException, PropertyNotFoundException, PropertyUnavailableException {

        if (buyerId <= 0) {
            throw new IllegalArgumentException("Invalid buyer ID");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        propertyService.requireAvailable(propertyId);

        String sql = "INSERT INTO property_requests (property_id, buyer_id, message, status) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, propertyId);
            ps.setInt(2, buyerId);
            ps.setString(3, message.trim());
            ps.setString(4, RequestStatus.PENDING.name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    public PropertyRequest getRequestById(int requestId) throws SQLException {
        String sql = "SELECT * FROM property_requests WHERE request_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRequest(rs);
                }
            }
        }

        return null;
    }

    public List<PropertyRequest> getRequestsByBuyer(int buyerId) throws SQLException {
        String sql = "SELECT * FROM property_requests WHERE buyer_id = ? ORDER BY created_at DESC";
        List<PropertyRequest> requests = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRequest(rs));
                }
            }
        }

        return requests;
    }

    public List<PropertyRequest> getRequestsForOwner(int ownerId) throws SQLException {
        String sql = "SELECT pr.* FROM property_requests pr "
                + "JOIN properties p ON pr.property_id = p.property_id "
                + "WHERE p.owner_id = ? ORDER BY pr.created_at DESC";

        List<PropertyRequest> requests = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRequest(rs));
                }
            }
        }

        return requests;
    }

    public void updateMessage(int buyerId, int requestId, String message)
            throws SQLException, UnauthorizedOperationException {

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        PropertyRequest request = getRequestById(requestId);

        if (request == null || request.getBuyerId() != buyerId) {
            throw new UnauthorizedOperationException("You cannot update this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be updated");
        }

        String sql = "UPDATE property_requests SET message = ? WHERE request_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, message.trim());
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

    public void acceptRequest(int ownerId, int requestId)
            throws SQLException, PropertyNotFoundException, UnauthorizedOperationException {

        changeStatus(ownerId, requestId, RequestStatus.ACCEPTED);
    }

    public void rejectRequest(int ownerId, int requestId)
            throws SQLException, PropertyNotFoundException, UnauthorizedOperationException {

        changeStatus(ownerId, requestId, RequestStatus.REJECTED);
    }

    public void cancelRequest(int buyerId, int requestId)
            throws SQLException, UnauthorizedOperationException {

        PropertyRequest request = getRequestById(requestId);

        if (request == null || request.getBuyerId() != buyerId) {
            throw new UnauthorizedOperationException("You cannot cancel this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be cancelled");
        }

        updateStatus(requestId, RequestStatus.CANCELLED);
    }

    private void changeStatus(int ownerId, int requestId, RequestStatus status)
            throws SQLException, PropertyNotFoundException, UnauthorizedOperationException {

        PropertyRequest request = getRequestById(requestId);

        if (request == null) {
            throw new IllegalArgumentException("Request not found");
        }

        Property property = propertyService.getPropertyById(request.getPropertyId());

        if (property.getOwnerId() != ownerId) {
            throw new UnauthorizedOperationException("You cannot manage this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be changed");
        }

        updateStatus(requestId, status);
    }

    private void updateStatus(int requestId, RequestStatus status) throws SQLException {
        String sql = "UPDATE property_requests SET status = ? WHERE request_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

    private PropertyRequest mapRequest(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("created_at");

        LocalDateTime createdAt = timestamp == null ? null : timestamp.toLocalDateTime();

        return new PropertyRequest(
                rs.getInt("request_id"),
                rs.getInt("property_id"),
                rs.getInt("buyer_id"),
                rs.getString("message"),
                RequestStatus.valueOf(rs.getString("status")),
                createdAt);
    }
}
