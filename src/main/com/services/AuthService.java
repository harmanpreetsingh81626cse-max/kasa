package main.com.services;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import main.com.dao.UserDAO;
import main.com.models.User;
import main.com.models.Buyer;
import main.com.models.Owner;
import main.com.Sequrity.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public int registerBuyer(String username, String email, String password,
            String phone, String address) throws SQLException {

        validateRegistration(username, email, password);

        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        Buyer buyer = new Buyer(
                0,
                username,
                email,
                hashedPassword,
                phone,
                LocalDateTime.now(),
                address);

        return userDAO.saveUser(buyer);
    }

    public int registerOwner(String username, String email, String password,
            String phone) throws SQLException {

        validateRegistration(username, email, password);

        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String hashedPassword = PasswordUtil.hashPassword(password);

        Owner owner = new Owner(
                0,
                username,
                email,
                hashedPassword,
                phone,
                LocalDateTime.now(),
                false);

        return userDAO.saveUser(owner);
    }

    public User login(String email, String password) throws SQLException {

        if (email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email and password are required");
        }

        Optional<User> optionalUser = userDAO.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = optionalUser.get();

        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }

    private void validateRegistration(String username, String email,
            String password) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }
    }
}