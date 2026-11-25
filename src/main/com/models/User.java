package main.com.models;

import java.time.LocalDateTime;
import java.util.Scanner;
import main.com.enums.UserRole;

public abstract class User {
    private int userId;
    private String userName;
    private String email;
    private String password;
    private String phone;
    private UserRole role;
    private LocalDateTime createdAt;

    protected User(int userId, String userName, String email, String password, String phone, LocalDateTime CreatedAt,
            UserRole role) {

        if (userId < 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (role == null) {
            throw new IllegalArgumentException("User role is required");
        }

        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.createdAt = CreatedAt;
        this.role = role;

    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public String getPhone() {
        return phone;

    }

    public String getPasswordHash() {
        return password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void updateProfile(String name, String email, String phone) {

        this.userName = name;
        this.email = email;
        this.phone = phone;

    }

    public void changePassword(String newPasswordHash) {

        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new IllegalArgumentException("Invalid password hash");
        }

        this.password = newPasswordHash;
    }

    public void displayProfile() {
        System.out.println("User Name : " + userName);
        System.out.println("Email : " + email);
        System.out.println("Phone : " + phone);

    }

}