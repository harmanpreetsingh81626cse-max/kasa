package main.com.ui;

import java.util.Scanner;

import main.com.enums.UserRole;
import main.com.models.User;

public class MainMenu {
    private Scanner sc = new Scanner(System.in);
    private LoginMenu loginMenu = new LoginMenu(sc);
    private RegistrationMenu registrationMenu = new RegistrationMenu(sc);

    private void showHeader() {
        System.out.println("\n========================================");
        System.out.println("                K A S A");
        System.out.println("       PROPERTY MANAGEMENT SYSTEM");
        System.out.println("========================================");
    }

    public void showMenu() {
        int choice = 0;

        do {
            showHeader();

            System.out.println("\n              MAIN MENU");
            System.out.println("----------------------------------------");
            System.out.println("  [1]  Login");
            System.out.println("  [2]  Create an Account");
            System.out.println("  [3]  Exit");
            System.out.println("----------------------------------------");
            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("\nPlease enter a valid number.");
                sc.nextLine();
                continue;
            }

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    User user = loginMenu.login();

                    if (user != null) {
                        if (user.getRole() == UserRole.BUYER) {
                            new BuyerMenu(sc, user).showMenu();
                        } else if (user.getRole() == UserRole.OWNER) {
                            new OwnerMenu(sc, user).showMenu();
                        } else if (user.getRole() == UserRole.ADMIN) {
                            new AdminMenu(sc).showMenu();
                        }
                    }
                    break;

                case 2:
                    registrationMenu.showMenu();
                    break;

                case 3:
                    System.out.println("\nThank you for using Kasa!");
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }

            if (choice != 3) {
                System.out.println("\nPress Enter to continue...");
                sc.nextLine();
            }

        } while (choice != 3);
    }
}
