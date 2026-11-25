package main.com.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import main.com.config.DatabaseConnection;
import main.com.enums.PropertyStatus;
import main.com.exceptions.PropertyNotFoundException;
import main.com.exceptions.PropertyUnavailableException;
import main.com.models.Property;


public class AdminPropertyService {
    private final PropertyService propertyService = new PropertyService();

    public List<Property> getPendingProperties() throws SQLException {
        return propertyService.getPropertiesByStatus(PropertyStatus.PENDING);
    }

    public List<Property> getAllProperties() throws SQLException {
        return propertyService.getAllProperties();
    }

    public List<Property> getPropertiesByStatus(PropertyStatus status) throws SQLException {
        return propertyService.getPropertiesByStatus(status);
    }

    public void approveProperty(int propertyId)
            throws SQLException, PropertyNotFoundException, PropertyUnavailableException {
        verify(propertyId, PropertyStatus.AVAILABLE);
    }


    public void rejectProperty(int propertyId)
            throws SQLException, PropertyNotFoundException, PropertyUnavailableException {
        verify(propertyId, PropertyStatus.REJECTED);
    }

    
    public Map<PropertyStatus, Long> countByStatus() throws SQLException {
        Map<PropertyStatus, Long> counts = new EnumMap<>(PropertyStatus.class);
        for (PropertyStatus s : PropertyStatus.values()) {
            counts.put(s, 0L);
        }

        String sql = "SELECT status, COUNT(*) AS total FROM properties GROUP BY status";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(PropertyStatus.valueOf(rs.getString("status")), rs.getLong("total"));
            }
        }
        return counts;
    }

    private void verify(int propertyId, PropertyStatus newStatus)
            throws SQLException, PropertyNotFoundException, PropertyUnavailableException {

        String sql = "UPDATE properties SET status = ? WHERE property_id = ? AND status = 'PENDING'";
        int changed;
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setInt(2, propertyId);
            changed = ps.executeUpdate();
        }

        if (changed == 0) {
            PropertyStatus current = propertyService.getStatus(propertyId);
            throw new PropertyUnavailableException(
                    "Property " + propertyId + " is already " + current + "; only PENDING properties can be verified");
        }
    }
}
