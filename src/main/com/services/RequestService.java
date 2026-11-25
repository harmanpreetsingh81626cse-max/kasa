package main.com.services;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import main.com.dao.PropertyRequestDAO;
import main.com.enums.RequestStatus;
import main.com.enums.UserRole;
import main.com.models.Property;
import main.com.models.PropertyRequest;
import main.com.models.User;
import main.com.exceptions.PropertyNotFoundException;

public class RequestService {

    private final PropertyRequestDAO requestDAO = new PropertyRequestDAO();
    private final PropertyService propertyService = new PropertyService();

    public int createRequest(User buyer, int propertyId, String message)
            throws SQLException, PropertyNotFoundException {

        if (buyer == null || buyer.getRole() != UserRole.BUYER) {
            throw new IllegalArgumentException("Only buyers can create requests");
        }

        Property property = propertyService.getPropertyById(propertyId);

        if (property.getStatus() != main.com.enums.PropertyStatus.AVAILABLE) {
            throw new IllegalArgumentException("Property is not available");
        }

        if (property.getOwnerId() == buyer.getUserId()) {
            throw new IllegalArgumentException("You cannot request your own property");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Request message is required");
        }

        PropertyRequest request = new PropertyRequest(
                0,
                propertyId,
                buyer.getUserId(),
                message,
                RequestStatus.PENDING,
                LocalDateTime.now());

        return requestDAO.addRequest(request);
    }

    public List<PropertyRequest> getBuyerRequests(int buyerId)
            throws SQLException, PropertyNotFoundException {

        return requestDAO.findByBuyerId(buyerId);
    }

    public List<PropertyRequest> getOwnerRequests(int ownerId)
            throws SQLException {

        return requestDAO.findByOwnerId(ownerId);
    }

    public void acceptRequest(User owner, int requestId)
            throws SQLException, PropertyNotFoundException {

        if (owner == null || owner.getRole() != UserRole.OWNER) {
            throw new IllegalArgumentException("Only owners can accept requests");
        }

        PropertyRequest request = requestDAO.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        Property property = propertyService.getPropertyById(request.getPropertyId());

        if (property.getOwnerId() != owner.getUserId()) {
            throw new IllegalArgumentException("You cannot manage this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Request is not pending");
        }

        if (property.getStatus() != main.com.enums.PropertyStatus.AVAILABLE) {
            throw new IllegalArgumentException("Property is no longer available");
        }

        requestDAO.updateStatus(requestId, RequestStatus.ACCEPTED);
    }

    public void rejectRequest(User owner, int requestId)
            throws SQLException, PropertyNotFoundException {

        if (owner == null || owner.getRole() != UserRole.OWNER) {
            throw new IllegalArgumentException("Only owners can reject requests");
        }

        PropertyRequest request = requestDAO.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        Property property = propertyService.getPropertyById(request.getPropertyId());

        if (property.getOwnerId() != owner.getUserId()) {
            throw new IllegalArgumentException("You cannot manage this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Request is not pending");
        }

        requestDAO.updateStatus(requestId, RequestStatus.REJECTED);
    }

    public void cancelRequest(User buyer, int requestId)
            throws SQLException {

        if (buyer == null || buyer.getRole() != UserRole.BUYER) {
            throw new IllegalArgumentException("Only buyers can cancel requests");
        }

        PropertyRequest request = requestDAO.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getBuyerId() != buyer.getUserId()) {
            throw new IllegalArgumentException("You cannot cancel this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Only pending requests can be cancelled");
        }

        requestDAO.updateStatus(requestId, RequestStatus.CANCELLED);
    }
}