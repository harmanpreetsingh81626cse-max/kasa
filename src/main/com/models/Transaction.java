package main.com.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import main.com.enums.TransactionStatus;

public class Transaction {

    private int transactionId;
    private int propertyId;
    private int buyerId;
    private int sellerId;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime transactionDate;

    public Transaction(int transactionId, int propertyId, int buyerId, int sellerId, BigDecimal amount,
            TransactionStatus status, LocalDateTime transactionDate) {

        if (transactionId < 0) {
            throw new IllegalArgumentException("Invalid transaction ID");
        }

        if (propertyId <= 0) {
            throw new IllegalArgumentException("Invalid property ID");
        }

        if (buyerId <= 0 || sellerId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (buyerId == sellerId) {
            throw new IllegalArgumentException("Buyer and seller cannot be the same");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (status == null) {
            throw new IllegalArgumentException("Transaction status cannot be null");
        }

        if (transactionDate == null) {
            throw new IllegalArgumentException("Transaction date cannot be null");
        }
        this.transactionId = transactionId;
        this.propertyId = propertyId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
        this.status = status;
        this.transactionDate = transactionDate;

    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public boolean isCompleted() {
        return status == TransactionStatus.COMPLETED;
    }

    public void changeStatus(TransactionStatus status) {
        if (this.status == TransactionStatus.COMPLETED) {
            return;
        }
        this.status = status;
    }

}
