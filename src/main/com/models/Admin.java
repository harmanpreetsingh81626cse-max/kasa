package main.com.models;

import java.time.LocalDateTime;

import main.com.enums.UserRole;

public class Admin extends User {
    public Admin(int id, String name, String email, String phone, String password, LocalDateTime date) {
        super(id, name, email, password, phone, date, UserRole.ADMIN);

    }

}
