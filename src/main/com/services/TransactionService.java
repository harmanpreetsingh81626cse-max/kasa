package main.com.services;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import main.com.dao.TransactionDAO;
import main.com.enums.PropertyStatus;
import main.com.enums.TransactionStatus;
import main.com.enums.UserRole;
import main.com.exceptions.PropertyNotFoundException;
import main.com.models.Property;
import main.com.models.Transaction;
import main.com.models.User;

public class TransactionService {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final PropertyService propertyService = new PropertyService();

    public int purchaseProperty(User buyer, int propertyId)
            throws SQLException, PropertyNotFoundException {

        if (buyer == null || buyer.getRole() != UserRole.BUYER) {
            throw new IllegalArgumentException("Only buyers can purchase properties");
        }

        Property property = propertyService.getPropertyById(propertyId);

        if (property.getStatus() != PropertyStatus.AVAILABLE) {
            throw new IllegalArgumentException("Property is not available");
        }

        if (property.getOwnerId() == buyer.getUserId()) {
            throw new IllegalArgumentException("You cannot purchase your own property");
        }

        BigDecimal amount = property.getPrice();

        Transaction transaction = new Transaction(
                0,
                propertyId,
                buyer.getUserId(),
                property.getOwnerId(),
                amount,
                TransactionStatus.COMPLETED,
                LocalDateTime.now());

        int transactionId = transactionDAO.addTransaction(transaction);

        propertyService.updateStatus(propertyId, PropertyStatus.SOLD);

        return transactionId;
    }

    public List<Transaction> getBuyerTransactions(int buyerId)
            throws SQLException {

        return transactionDAO.findByBuyerId(buyerId);
    }

    public List<Transaction> getSellerTransactions(int sellerId)
            throws SQLException {

        return transactionDAO.findBySellerId(sellerId);
    }

    public Transaction getTransaction(int transactionId)
            throws SQLException {

        return transactionDAO.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }
}