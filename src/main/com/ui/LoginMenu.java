package main.com.ui;

import java.util.Scanner;
import main.com.services.AuthService;
import main.com.models.User;

public class LoginMenu {
    private Scanner sc;
    private AuthService auth = new AuthService();

    public LoginMenu(Scanner sc) {
        this.sc = sc;
    }

    private void showHeader() {
        System.out.println("\n================================");
        System.out.println("             K A S A");
        System.out.println("     PROPERTY MANAGEMENT SYSTEM");
        System.out.println("================================");
    }

    public User login() {
        showHeader();

        System.out.println("\n             LOGIN");
        System.out.println("--------------------------------");

        System.out.print("Email    : ");
        String email = sc.nextLine();

        System.out.print("Password : ");
        String password;

        if (System.console() != null) {
            password = new String(System.console().readPassword());
        } else {
            password = sc.nextLine();
        }

        try {
            User user = auth.login(email, password);

            System.out.println("\n--------------------------------");
            System.out.println("Login successful!");
            System.out.println("Welcome, " + user.getUserName());
            System.out.println("Role: " + user.getRole());
            System.out.println("--------------------------------");

            return user;

        } catch (Exception e) {
            System.out.println("\nLogin failed: " + e.getMessage());
            return null;
        }
    }
}