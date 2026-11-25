package main.com.dao;

import main.com.config.DatabaseConnection;
import main.com.models.Review;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReviewDAO {

    // 1. ADD REVIEW
    public int addReview(Review review) throws SQLException {

        String sql = """
                INSERT INTO reviews
                (buyer_id, owner_id, rating, comment)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, review.getBuyerId());
            ps.setInt(2, review.getOwnerId());
            ps.setInt(3, review.getRating());
            ps.setString(4, review.getComment());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    // 2. FIND REVIEW BY ID
    public Optional<Review> findById(int reviewId) throws SQLException {

        String sql = """
                SELECT * FROM reviews
                WHERE review_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reviewId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapReview(rs));
                }
            }
        }

        return Optional.empty();
    }

    // 3. FIND REVIEWS BY BUYER
    public List<Review> findByBuyerId(int buyerId) throws SQLException {

        List<Review> reviews = new ArrayList<>();

        String sql = """
                SELECT * FROM reviews
                WHERE buyer_id = ?
                ORDER BY created_at DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, buyerId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    reviews.add(mapReview(rs));
                }
            }
        }

        return reviews;
    }

    // 4. FIND REVIEWS RECEIVED BY OWNER
    public List<Review> findByOwnerId(int ownerId) throws SQLException {

        List<Review> reviews = new ArrayList<>();

        String sql = """
                SELECT * FROM reviews
                WHERE owner_id = ?
                ORDER BY created_at DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    reviews.add(mapReview(rs));
                }
            }
        }

        return reviews;
    }

    // 5. UPDATE REVIEW
    public boolean updateReview(int reviewId, int buyerId,
            int rating, String comment)
            throws SQLException {

        String sql = """
                UPDATE reviews
                SET rating = ?, comment = ?
                WHERE review_id = ? AND buyer_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, reviewId);
            ps.setInt(4, buyerId);

            return ps.executeUpdate() > 0;
        }
    }

    // 6. DELETE REVIEW
    public boolean deleteReview(int reviewId, int buyerId)
            throws SQLException {

        String sql = """
                DELETE FROM reviews
                WHERE review_id = ? AND buyer_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reviewId);
            ps.setInt(2, buyerId);

            return ps.executeUpdate() > 0;
        }
    }

    // 7. CONVERT RESULTSET INTO REVIEW OBJECT
    private Review mapReview(ResultSet rs) throws SQLException {

        Timestamp timestamp = rs.getTimestamp("created_at");

        LocalDateTime createdAt = timestamp != null
                ? timestamp.toLocalDateTime()
                : null;

        return new Review(
                rs.getInt("review_id"),
                rs.getInt("buyer_id"),
                rs.getInt("owner_id"),
                rs.getInt("rating"),
                rs.getString("comment"),
                createdAt);
    }
}