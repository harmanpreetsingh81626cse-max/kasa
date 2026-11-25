package main.com.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Timestamp;
import java.sql.Statement;

import main.com.config.DatabaseConnection;
import main.com.enums.ListingType;
import main.com.enums.PropertyStatus;
import main.com.enums.PropertyType;
import main.com.models.Property;

public class PropertyDAO {

    public int addProperty(Property property) throws SQLException {

        String sql = """
                INSERT INTO properties
                (owner_id, title, description, location, city, state, pincode,
                 property_type, listing_type, price, area, bedrooms, bathrooms, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, property.getOwnerId());
            ps.setString(2, property.getTitle());
            ps.setString(3, property.getDescription());
            ps.setString(4, property.getLocation());
            ps.setString(5, property.getCity());
            ps.setString(6, property.getState());
            ps.setString(7, property.getPincode());
            ps.setString(8, property.getPropertyType().name());
            ps.setString(9, property.getListingType().name());
            ps.setBigDecimal(10, property.getPrice());
            ps.setBigDecimal(11, property.getArea());
            ps.setInt(12, property.getBedrooms());
            ps.setInt(13, property.getBathrooms());
            ps.setString(14, property.getStatus().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    public Optional<Property> findById(int propertyId) throws SQLException {

        String sql = "SELECT * FROM properties WHERE property_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, propertyId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapProperty(rs));
                }
            }
        }

        return Optional.empty();
    }

    public List<Property> findByOwnerId(int ownerId) throws SQLException {

        List<Property> properties = new ArrayList<>();

        String sql = "SELECT * FROM properties WHERE owner_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    properties.add(mapProperty(rs));
                }
            }
        }

        return properties;
    }

    public List<Property> findAllApproved() throws SQLException {

        List<Property> properties = new ArrayList<>();

        String sql = """
                SELECT * FROM properties
                WHERE status = ?
                ORDER BY created_at DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, PropertyStatus.APPROVED.name());

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    properties.add(mapProperty(rs));
                }
            }
        }

        return properties;
    }

    public List<Property> searchProperties(
            String city,
            PropertyType propertyType,
            BigDecimal minPrice,
            BigDecimal maxPrice) throws SQLException {

        List<Property> properties = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM properties WHERE status = ?");

        List<Object> parameters = new ArrayList<>();

        parameters.add(PropertyStatus.APPROVED.name());

        if (city != null && !city.isBlank()) {
            sql.append(" AND city LIKE ?");
            parameters.add("%" + city + "%");
        }

        if (propertyType != null) {
            sql.append(" AND property_type = ?");
            parameters.add(propertyType.name());
        }

        if (minPrice != null) {
            sql.append(" AND price >= ?");
            parameters.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            parameters.add(maxPrice);
        }

        sql.append(" ORDER BY created_at DESC");

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    properties.add(mapProperty(rs));
                }
            }
        }

        return properties;
    }

    // 6. UPDATE PROPERTY
    public boolean updateProperty(Property property) throws SQLException {

        String sql = """
                UPDATE properties
                SET title = ?, description = ?, location = ?,
                    city = ?, state = ?, pincode = ?,
                    property_type = ?, listing_type = ?,
                    price = ?, area = ?, bedrooms = ?, bathrooms = ?
                WHERE property_id = ? AND owner_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, property.getTitle());
            ps.setString(2, property.getDescription());
            ps.setString(3, property.getLocation());
            ps.setString(4, property.getCity());
            ps.setString(5, property.getState());
            ps.setString(6, property.getPincode());
            ps.setString(7, property.getPropertyType().name());
            ps.setString(8, property.getListingType().name());
            ps.setBigDecimal(9, property.getPrice());
            ps.setBigDecimal(10, property.getArea());
            ps.setInt(11, property.getBedrooms());
            ps.setInt(12, property.getBathrooms());
            ps.setInt(13, property.getPropertyId());
            ps.setInt(14, property.getOwnerId());

            return ps.executeUpdate() > 0;
        }
    }

    // 7. DELETE PROPERTY
    public boolean deleteProperty(int propertyId, int ownerId) throws SQLException {

        String sql = """
                DELETE FROM properties
                WHERE property_id = ? AND owner_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, propertyId);
            ps.setInt(2, ownerId);

            return ps.executeUpdate() > 0;
        }
    }

    private Property mapProperty(ResultSet rs) throws SQLException {

        Timestamp timestamp = rs.getTimestamp("created_at");

        LocalDateTime createdAt = timestamp != null
                ? timestamp.toLocalDateTime()
                : null;

        return new Property(
                rs.getInt("property_id"),
                rs.getInt("owner_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("location"),
                rs.getString("city"),
                rs.getString("state"),
                rs.getString("pincode"),
                PropertyType.valueOf(rs.getString("property_type")),
                ListingType.valueOf(rs.getString("listing_type")),
                rs.getBigDecimal("price"),
                rs.getBigDecimal("area"),
                rs.getInt("bedrooms"),
                rs.getInt("bathrooms"),
                PropertyStatus.valueOf(rs.getString("status")),
                createdAt);
    }

}
