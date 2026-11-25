package main.com.models;

import java.time.LocalDateTime;

import main.com.enums.UserRole;

public class Buyer extends User {
    private String address;

    public Buyer(int userId, String userName, String email, String password, String phone, LocalDateTime CreatedAt,
            String address) {
        super(userId, userName, email, password, phone, CreatedAt, UserRole.BUYER);
        this.address = address;

    }

    public String getAddress() {
        return address;

    }

    public void updateAddress(String Address) {
        this.address = Address;
    }

    @Override
    public void displayProfile() {
        super.displayProfile();
        System.out.println("Address : " + address);
    }

}
