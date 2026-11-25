package main.com.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Review {
    private int reviewId;
    private int buyerId;
    private int ownerId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public Review(int reviewId, int buyerId, int ownerId, int rating, String comment, LocalDateTime createdAt) {

        if (reviewId < 0) {
            throw new IllegalArgumentException("Invalid review ID");
        }

        if (buyerId <= 0 || ownerId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (buyerId == ownerId) {
            throw new IllegalArgumentException("Buyer and owner cannot be the same");
        }

        validateReview(rating, comment);

        if (createdAt == null) {
            throw new IllegalArgumentException("Review date cannot be null");
        }

        this.reviewId = reviewId;
        this.buyerId = buyerId;
        this.ownerId = ownerId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    private void validateReview(int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
    }

    public int getRating() {
        return rating;
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getReviewDate() {
        return createdAt;
    }

    public void updateReview(int rating, String comment) {

        validateReview(rating, comment);
        this.rating = rating;
        this.comment = comment.trim();

    }

    @Override
    public String toString() {
        return "Review ID: " + reviewId +
                "\nBuyer ID: " + buyerId +
                "\nOwner ID: " + ownerId +
                "\nRating: " + rating + "/5" +
                "\nComment: " + comment +
                "\nCreated At: " + createdAt;
    }

}
