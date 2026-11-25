package main.com.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import main.com.config.DatabaseConnection;
import main.com.enums.ListingType;
import main.com.enums.PropertyStatus;
import main.com.enums.PropertyType;
import main.com.exceptions.PropertyNotFoundException;
import main.com.exceptions.PropertyUnavailableException;
import main.com.models.Property;


public class PropertyService {

    private static final String COLUMNS = "property_id, owner_id, title, description, location, city, state, "
            + "pincode, property_type, listing_type, price, area, bedrooms, bathrooms, status, created_at";

    public int addProperty(int ownerId, String title, String description, String location, String city,
            String state, String pincode, PropertyType propertyType, ListingType listingType,
            BigDecimal price, BigDecimal area, int bedrooms, int bathrooms) throws SQLException {

        validateDetails(title, location, price);
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }
        if (state == null || state.trim().isEmpty()) {
            throw new IllegalArgumentException("State cannot be empty");
        }
        if (pincode == null || !pincode.matches("\\d{6}")) {
            throw new IllegalArgumentException("Pincode must be exactly 6 digits");
        }
        if (propertyType == null || listingType == null) {
            throw new IllegalArgumentException("Property type and listing type are required");
        }
        if (area == null || area.signum() <= 0) {
            throw new IllegalArgumentException("Area must be greater than 0");
        }
        if (bedrooms < 0 || bathrooms < 0) {
            throw new IllegalArgumentException("Bedrooms and bathrooms cannot be negative");
        }

        String sql = "INSERT INTO properties (owner_id, title, description, location, city, state, pincode, "
                + "property_type, listing_type, price, area, bedrooms, bathrooms, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection()) {
            if (!isOwner(con, ownerId)) {
                throw new IllegalArgumentException("User " + ownerId + " is not a registered owner");
            }

            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, ownerId);
                ps.setString(2, title.trim());
                ps.setString(3, description);
                ps.setString(4, location.trim());
                ps.setString(5, city.trim());
                ps.setString(6, state.trim());
                ps.setString(7, pincode);
                ps.setString(8, propertyType.name());
                ps.setString(9, listingType.name());
                ps.setBigDecimal(10, price);
                ps.setBigDecimal(11, area);
                ps.setInt(12, bedrooms);
                ps.setInt(13, bathrooms);
                ps.setString(14, PropertyStatus.PENDING.name());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }


    public void updateProperty(int ownerId, int propertyId, String title, String description, String location,
            BigDecimal price) throws SQLException, PropertyNotFoundException, PropertyUnavailableException {

        validateDetails(title, location, price);

        try (Connection con = DatabaseConnection.getConnection()) {
            Basics b = loadBasics(con, propertyId);
            checkOwnership(b, ownerId, "edit");
            if (b.status == PropertyStatus.SOLD || b.status == PropertyStatus.RENTED) {
                throw new PropertyUnavailableException(
                        "Property " + propertyId + " is " + b.status + " and can no longer be edited");
            }

            PropertyStatus newStatus = (b.status == PropertyStatus.REJECTED) ? PropertyStatus.PENDING : b.status;
            String sql = "UPDATE properties SET title = ?, description = ?, location = ?, price = ?, status = ? "
                    + "WHERE property_id = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, title.trim());
                ps.setString(2, description);
                ps.setString(3, location.trim());
                ps.setBigDecimal(4, price);
                ps.setString(5, newStatus.name());
                ps.setInt(6, propertyId);
                ps.executeUpdate();
            }
        }
    }


    public void deleteProperty(int ownerId, int propertyId)
            throws SQLException, PropertyNotFoundException, PropertyUnavailableException {

        try (Connection con = DatabaseConnection.getConnection()) {
            Basics b = loadBasics(con, propertyId);
            checkOwnership(b, ownerId, "delete");
            if (b.status == PropertyStatus.SOLD || b.status == PropertyStatus.RENTED) {
                throw new PropertyUnavailableException(
                        "Property " + propertyId + " is " + b.status + " and cannot be deleted");
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM properties WHERE property_id = ?")) {
                ps.setInt(1, propertyId);
                ps.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                throw new PropertyUnavailableException(
                        "Property " + propertyId + " has requests or transactions and cannot be deleted");
            }
        }
    }


    public Property getPropertyById(int propertyId) throws SQLException, PropertyNotFoundException {
        List<Object> params = new ArrayList<>();
        params.add(propertyId);
        List<Property> found = query("SELECT " + COLUMNS + " FROM properties WHERE property_id = ?", params);
        if (found.isEmpty()) {
            throw new PropertyNotFoundException(propertyId);
        }
        return found.get(0);
    }

    public List<Property> getAvailableProperties() throws SQLException {
        return getPropertiesByStatus(PropertyStatus.AVAILABLE);
    }


    public List<Property> getPropertiesByOwner(int ownerId) throws SQLException {
        List<Object> params = new ArrayList<>();
        params.add(ownerId);
        return query("SELECT " + COLUMNS + " FROM properties WHERE owner_id = ? ORDER BY created_at DESC", params);
    }

    public List<Property> getPropertiesByStatus(PropertyStatus status) throws SQLException {
        List<Object> params = new ArrayList<>();
        params.add(status.name());
        return query("SELECT " + COLUMNS + " FROM properties WHERE status = ? ORDER BY price ASC", params);
    }

    public List<Property> getAllProperties() throws SQLException {
        return query("SELECT " + COLUMNS + " FROM properties ORDER BY created_at DESC", new ArrayList<>());
    }


    public List<Property> searchProperties(String city, PropertyType propertyType, ListingType listingType,
            BigDecimal minPrice, BigDecimal maxPrice, Integer minBedrooms) throws SQLException {

        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }

        StringBuilder sql = new StringBuilder("SELECT " + COLUMNS + " FROM properties WHERE status = ?");
        List<Object> params = new ArrayList<>();
        params.add(PropertyStatus.AVAILABLE.name());

        if (city != null && !city.trim().isEmpty()) {
            sql.append(" AND LOWER(city) = LOWER(?)");
            params.add(city.trim());
        }
        if (propertyType != null) {
            sql.append(" AND property_type = ?");
            params.add(propertyType.name());
        }
        if (listingType != null) {
            sql.append(" AND listing_type = ?");
            params.add(listingType.name());
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        if (minBedrooms != null) {
            sql.append(" AND bedrooms >= ?");
            params.add(minBedrooms);
        }
        sql.append(" ORDER BY price ASC");

        return query(sql.toString(), params);
    }


    public PropertyStatus getStatus(int propertyId) throws SQLException, PropertyNotFoundException {
        try (Connection con = DatabaseConnection.getConnection()) {
            return loadBasics(con, propertyId).status;
        }
    }

    public Property requireAvailable(int propertyId)
            throws SQLException, PropertyNotFoundException, PropertyUnavailableException {
        Property p = getPropertyById(propertyId);
        PropertyStatus status = getStatus(propertyId);
        if (status != PropertyStatus.AVAILABLE) {
            throw new PropertyUnavailableException(propertyId, status);
        }
        return p;
    }

    public void updateStatus(int propertyId, PropertyStatus newStatus) throws SQLException, PropertyNotFoundException {
        try (Connection con = DatabaseConnection.getConnection()) {
            loadBasics(con, propertyId); // throws PropertyNotFoundException if missing
            try (PreparedStatement ps = con.prepareStatement("UPDATE properties SET status = ? WHERE property_id = ?")) {
                ps.setString(1, newStatus.name());
                ps.setInt(2, propertyId);
                ps.executeUpdate();
            }
        }
    }

    private static void validateDetails(String title, String location, BigDecimal price) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
    }


    private static class Basics {
        final int ownerId;
        final PropertyStatus status;

        Basics(int ownerId, PropertyStatus status) {
            this.ownerId = ownerId;
            this.status = status;
        }
    }

    private Basics loadBasics(Connection con, int propertyId) throws SQLException, PropertyNotFoundException {
        String sql = "SELECT owner_id, status FROM properties WHERE property_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, propertyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Basics(rs.getInt("owner_id"), PropertyStatus.valueOf(rs.getString("status")));
                }
            }
        }
        throw new PropertyNotFoundException(propertyId);
    }

    private static void checkOwnership(Basics b, int ownerId, String action) {
        if (b.ownerId != ownerId) {
            throw new SecurityException("You can only " + action + " your own properties");
        }
    }

    private boolean isOwner(Connection con, int userId) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE user_id = ? AND role = 'OWNER'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private List<Property> query(String sql, List<Object> params) throws SQLException {
        List<Property> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i)); // JDBC parameters start at 1
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private Property mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("created_at");
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
                ts == null ? null : ts.toLocalDateTime());
    }
}
