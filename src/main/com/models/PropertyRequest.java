package main.com.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import main.com.enums.RequestStatus;

public class PropertyRequest {
    private int requestId;
    private int propertyId;
    private int buyerId;
    private String message;
    private RequestStatus status;
    private LocalDateTime createdAt;

    public PropertyRequest(int requestId, int propertyId, int buyerId, String message, RequestStatus status,
            LocalDateTime createdAt) {
        this.requestId = requestId;
        this.propertyId = propertyId;
        this.buyerId = buyerId;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public String getMessage() {
        return message;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public LocalDateTime getcreatedAt() {
        return createdAt;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void updateMessage(String message) {
        this.message = message;
    }

    public void changeStatus(RequestStatus status) {
        this.status = status;
    }

    public void displayRequest() {
        System.out.println("request id : " + requestId);
        System.out.println("Property id : " + propertyId);
        System.out.println("buyer id : " + buyerId);
        System.out.println("Status : " + status);
        System.out.println("message : " + message);

    }

}
