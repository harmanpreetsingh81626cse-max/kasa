package main.com.ui;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.com.models.Property;
import main.com.enums.PropertyStatus;
import main.com.services.AdminPropertyService;

public class AdminMenu {
    private Scanner sc;
    private AdminPropertyService adminService = new AdminPropertyService();

    public AdminMenu(Scanner sc) {
        this.sc = sc;
    }

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

            System.out.println("\n           ADMIN DASHBOARD");
            System.out.println("----------------------------------------");
            System.out.println("  [1]  View Pending Properties");
            System.out.println("  [2]  View All Properties");
            System.out.println("  [3]  Approve Property");
            System.out.println("  [4]  Reject Property");
            System.out.println("  [5]  Property Status Summary");
            System.out.println("  [6]  Logout");
            System.out.println("----------------------------------------");
            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("\nPlease enter a valid number.");
                sc.nextLine();
                continue;
            }

            choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {
                    case 1:
                        viewPendingProperties();
                        break;
                    case 2:
                        viewAllProperties();
                        break;
                    case 3:
                        approveProperty();
                        break;
                    case 4:
                        rejectProperty();
                        break;
                    case 5:
                        statusSummary();
                        break;
                    case 6:
                        System.out.println("\nLogging out...");
                        break;
                    default:
                        System.out.println("\nInvalid choice.");
                }
            } catch (Exception e) {
                System.out.println("\nError: " + e.getMessage());
            }

            if (choice != 6) {
                System.out.println("\nPress Enter to continue...");
                sc.nextLine();
            }

        } while (choice != 6);
    }

    private void viewPendingProperties() throws Exception {
        List<Property> properties = adminService.getPendingProperties();

        System.out.println("\n        PENDING PROPERTIES");
        System.out.println("----------------------------------------");

        if (properties.isEmpty()) {
            System.out.println("No pending properties.");
            return;
        }

        for (Property p : properties) {
            displayProperty(p);
        }
    }

    private void viewAllProperties() throws Exception {
        List<Property> properties = adminService.getAllProperties();

        System.out.println("\n           ALL PROPERTIES");
        System.out.println("----------------------------------------");

        if (properties.isEmpty()) {
            System.out.println("No properties found.");
            return;
        }

        for (Property p : properties) {
            displayProperty(p);
        }
    }

    private void approveProperty() throws Exception {
        System.out.print("\nEnter Property ID to approve: ");
        int id = Integer.parseInt(sc.nextLine());

        adminService.approveProperty(id);

        System.out.println("\nProperty approved successfully!");
    }

    private void rejectProperty() throws Exception {
        System.out.print("\nEnter Property ID to reject: ");
        int id = Integer.parseInt(sc.nextLine());

        adminService.rejectProperty(id);

        System.out.println("\nProperty rejected successfully!");
    }

    private void statusSummary() throws Exception {
        Map<PropertyStatus, Long> counts = adminService.countByStatus();

        System.out.println("\n        PROPERTY STATUS SUMMARY");
        System.out.println("----------------------------------------");

        for (PropertyStatus status : PropertyStatus.values()) {
            System.out.println(status + " : " + counts.get(status));
        }
    }

    private void displayProperty(Property p) {
        System.out.println("\n----------------------------------------");
        System.out.println("Property ID : " + p.getPropertyId());
        System.out.println("Owner ID    : " + p.getOwnerId());
        System.out.println("Title       : " + p.getTitle());
        System.out.println("Type        : " + p.getPropertyType());
        System.out.println("Listing     : " + p.getListingType());
        System.out.println("Location    : " + p.getLocation());
        System.out.println("City        : " + p.getCity());
        System.out.println("Price       : " + p.getPrice());
        System.out.println("Status      : " + p.getStatus());
        System.out.println("----------------------------------------");
    }
}
