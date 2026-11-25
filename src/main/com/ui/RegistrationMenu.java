package main.com.ui;

import java.util.Scanner;
import main.com.services.AuthService;

public class RegistrationMenu {
    private Scanner sc;
    private AuthService auth = new AuthService();

    public RegistrationMenu(Scanner sc) {
        this.sc = sc;
    }

    private void showHeader() {
        System.out.println("\n========================================");
        System.out.println("                K A S A");
        System.out.println("       PROPERTY MANAGEMENT SYSTEM");
        System.out.println("========================================");
    }

    public void showMenu() {
        showHeader();

        System.out.println("\n            REGISTRATION");
        System.out.println("----------------------------------------");
        System.out.println("  [1]  Register as Buyer");
        System.out.println("  [2]  Register as Owner");
        System.out.println("  [3]  Back to Main Menu");
        System.out.println("----------------------------------------");
        System.out.print("Enter your choice: ");

        if (!sc.hasNextInt()) {
            System.out.println("\nPlease enter a valid number.");
            sc.nextLine();
            return;
        }

        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                registerBuyer();
                break;
            case 2:
                registerOwner();
                break;
            case 3:
                return;
            default:
                System.out.println("\nInvalid choice.");
        }
    }

    private void registerBuyer() {
        System.out.println("\n          BUYER REGISTRATION");
        System.out.println("----------------------------------------");

        System.out.print("Name     : ");
        String name = sc.nextLine();

        System.out.print("Email    : ");
        String email = sc.nextLine();

        System.out.print("Password : ");
        String password = sc.nextLine();

        System.out.print("Phone    : ");
        String phone = sc.nextLine();

        System.out.print("Address  : ");
        String address = sc.nextLine();

        try {
            int id = auth.registerBuyer(name, email, password, phone, address);
            System.out.println("\nRegistration successful!");
            System.out.println("Your User ID: " + id);
        } catch (Exception e) {
            System.out.println("\nRegistration failed: " + e.getMessage());
        }
    }

    private void registerOwner() {
        System.out.println("\n          OWNER REGISTRATION");
        System.out.println("----------------------------------------");

        System.out.print("Name     : ");
        String name = sc.nextLine();

        System.out.print("Email    : ");
        String email = sc.nextLine();

        System.out.print("Password : ");
        String password = sc.nextLine();

        System.out.print("Phone    : ");
        String phone = sc.nextLine();

        try {
            int id = auth.registerOwner(name, email, password, phone);
            System.out.println("\nRegistration successful!");
            System.out.println("Your User ID: " + id);
        } catch (Exception e) {
            System.out.println("\nRegistration failed: " + e.getMessage());
        }
    }
}
