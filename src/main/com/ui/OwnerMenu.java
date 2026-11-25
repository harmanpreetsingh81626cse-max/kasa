package main.com.ui;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import main.com.models.*;
import main.com.enums.*;
import main.com.services.*;

public class OwnerMenu {
    private Scanner sc;
    private User owner;

    private PropertyService propertyService = new PropertyService();
    private PropertyRequestService requestService = new PropertyRequestService();
    private TransactionService transactionService = new TransactionService();

    public OwnerMenu(Scanner sc, User owner) {
        this.sc = sc;
        this.owner = owner;
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

            System.out.println("\n            OWNER DASHBOARD");
            System.out.println("----------------------------------------");
            System.out.println("  [1]  Add Property");
            System.out.println("  [2]  View My Properties");
            System.out.println("  [3]  Update Property");
            System.out.println("  [4]  Delete Property");
            System.out.println("  [5]  View Buyer Requests");
            System.out.println("  [6]  Accept Request");
            System.out.println("  [7]  Reject Request");
            System.out.println("  [8]  Transaction History");
            System.out.println("  [9]  Logout");
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
                        addProperty();
                        break;
                    case 2:
                        viewProperties();
                        break;
                    case 3:
                        updateProperty();
                        break;
                    case 4:
                        deleteProperty();
                        break;
                    case 5:
                        viewRequests();
                        break;
                    case 6:
                        acceptRequest();
                        break;
                    case 7:
                        rejectRequest();
                        break;
                    case 8:
                        transactionHistory();
                        break;
                    case 9:
                        System.out.println("\nLogging out...");
                        break;
                    default:
                        System.out.println("\nInvalid choice.");
                }
            } catch (Exception e) {
                System.out.println("\nError: " + e.getMessage());
            }

            if (choice != 9) {
                System.out.println("\nPress Enter to continue...");
                sc.nextLine();
            }

        } while (choice != 9);
    }

    private void addProperty() throws Exception {
        System.out.println("\n          ADD PROPERTY");
        System.out.println("----------------------------------------");

        System.out.print("Title       : ");
        String title = sc.nextLine();

        System.out.print("Description : ");
        String description = sc.nextLine();

        System.out.print("Location    : ");
        String location = sc.nextLine();

        System.out.print("City        : ");
        String city = sc.nextLine();

        System.out.print("State       : ");
        String state = sc.nextLine();

        System.out.print("Pincode     : ");
        String pincode = sc.nextLine();

        System.out.print("Property Type (HOUSE/APARTMENT/PLOT/COMMERCIAL): ");
        PropertyType type = PropertyType.valueOf(sc.nextLine().trim().toUpperCase());

        System.out.print("Listing Type (SALE/RENT): ");
        ListingType listing = ListingType.valueOf(sc.nextLine().trim().toUpperCase());

        System.out.print("Price       : ");
        BigDecimal price = new BigDecimal(sc.nextLine());

        System.out.print("Area        : ");
        BigDecimal area = new BigDecimal(sc.nextLine());

        System.out.print("Bedrooms    : ");
        int bedrooms = Integer.parseInt(sc.nextLine());

        System.out.print("Bathrooms   : ");
        int bathrooms = Integer.parseInt(sc.nextLine());

        int id = propertyService.addProperty(
                owner.getUserId(), title, description, location,
                city, state, pincode, type, listing, price,
                area, bedrooms, bathrooms);

        System.out.println("\nProperty submitted successfully!");
        System.out.println("Property ID: " + id);
        System.out.println("Status: PENDING ADMIN APPROVAL");
    }

    private void viewProperties() throws Exception {
        List<Property> properties =
                propertyService.getPropertiesByOwner(owner.getUserId());

        System.out.println("\n          MY PROPERTIES");
        System.out.println("----------------------------------------");

        if (properties.isEmpty()) {
            System.out.println("You have not listed any properties.");
            return;
        }

        for (Property p : properties) {
            displayProperty(p);
        }
    }

    private void updateProperty() throws Exception {
        System.out.print("\nEnter Property ID: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("New Title: ");
        String title = sc.nextLine();

        System.out.print("New Description: ");
        String description = sc.nextLine();

        System.out.print("New Location: ");
        String location = sc.nextLine();

        System.out.print("New Price: ");
        BigDecimal price = new BigDecimal(sc.nextLine());

        propertyService.updateProperty(
                owner.getUserId(), id, title,
                description, location, price);

        System.out.println("\nProperty updated successfully.");
    }

    private void deleteProperty() throws Exception {
        System.out.print("\nEnter Property ID: ");
        int id = Integer.parseInt(sc.nextLine());

        propertyService.deleteProperty(owner.getUserId(), id);

        System.out.println("\nProperty deleted successfully.");
    }

    private void viewRequests() throws Exception {
        List<PropertyRequest> requests =
                requestService.getRequestsForOwner(owner.getUserId());

        System.out.println("\n          BUYER REQUESTS");
        System.out.println("----------------------------------------");

        if (requests.isEmpty()) {
            System.out.println("No buyer requests found.");
            return;
        }

        for (PropertyRequest r : requests) {
            r.displayRequest();
            System.out.println("----------------------------------------");
        }
    }

    private void acceptRequest() throws Exception {
        System.out.print("\nEnter Request ID: ");
        int id = Integer.parseInt(sc.nextLine());

        requestService.acceptRequest(owner.getUserId(), id);

        System.out.println("\nRequest accepted successfully.");
    }

    private void rejectRequest() throws Exception {
        System.out.print("\nEnter Request ID: ");
        int id = Integer.parseInt(sc.nextLine());

        requestService.rejectRequest(owner.getUserId(), id);

        System.out.println("\nRequest rejected successfully.");
    }

    private void transactionHistory() throws Exception {
        List<Transaction> transactions =
                transactionService.getSellerTransactions(owner.getUserId());

        System.out.println("\n          TRANSACTION HISTORY");
        System.out.println("----------------------------------------");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Transaction t : transactions) {
            System.out.println("Transaction ID : " + t.getTransactionId());
            System.out.println("Property ID    : " + t.getPropertyId());
            System.out.println("Buyer ID       : " + t.getBuyerId());
            System.out.println("Amount         : " + t.getAmount());
            System.out.println("Status         : " + t.getStatus());
            System.out.println("Date           : " + t.getTransactionDate());
            System.out.println("----------------------------------------");
        }
    }

    private void displayProperty(Property p) {
        System.out.println("\n----------------------------------------");
        System.out.println("Property ID : " + p.getPropertyId());
        System.out.println("Title       : " + p.getTitle());
        System.out.println("Type        : " + p.getPropertyType());
        System.out.println("Listing     : " + p.getListingType());
        System.out.println("Location    : " + p.getLocation());
        System.out.println("City        : " + p.getCity());
        System.out.println("Price       : " + p.getPrice());
        System.out.println("Area        : " + p.getArea());
        System.out.println("Bedrooms    : " + p.getBedrooms());
        System.out.println("Bathrooms   : " + p.getBathrooms());
        System.out.println("Status      : " + p.getStatus());
        System.out.println("----------------------------------------");
    }
}
