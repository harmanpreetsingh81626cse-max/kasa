package main.com.ui;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import main.com.models.*;
import main.com.enums.*;
import main.com.services.*;

public class BuyerMenu {
    private Scanner sc;
    private User buyer;

    private PropertyService propertyService = new PropertyService();
    private PropertyRequestService requestService = new PropertyRequestService();
    private TransactionService transactionService = new TransactionService();

    public BuyerMenu(Scanner sc, User buyer) {
        this.sc = sc;
        this.buyer = buyer;
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

            System.out.println("\n           BUYER DASHBOARD");
            System.out.println("----------------------------------------");
            System.out.println("  [1]  Browse Properties");
            System.out.println("  [2]  Search Properties");
            System.out.println("  [3]  View Property Details");
            System.out.println("  [4]  Send Property Request");
            System.out.println("  [5]  My Requests");
            System.out.println("  [6]  Cancel Request");
            System.out.println("  [7]  Purchase Property");
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
                        browseProperties();
                        break;
                    case 2:
                        searchProperties();
                        break;
                    case 3:
                        viewProperty();
                        break;
                    case 4:
                        sendRequest();
                        break;
                    case 5:
                        viewRequests();
                        break;
                    case 6:
                        cancelRequest();
                        break;
                    case 7:
                        purchaseProperty();
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

    private void browseProperties() throws Exception {
        List<Property> properties = propertyService.getAvailableProperties();

        System.out.println("\n           AVAILABLE PROPERTIES");
        System.out.println("----------------------------------------");

        if (properties.isEmpty()) {
            System.out.println("No properties available.");
            return;
        }

        for (Property p : properties) {
            displayProperty(p);
        }
    }

    private void searchProperties() throws Exception {
        System.out.print("\nCity (leave blank for any): ");
        String city = sc.nextLine();

        PropertyType type = null;
        ListingType listing = null;
        BigDecimal min = null;
        BigDecimal max = null;
        Integer bedrooms = null;

        System.out.println("\nProperty Types: HOUSE, APARTMENT, PLOT, COMMERCIAL");
        System.out.print("Property type (blank for any): ");
        String input = sc.nextLine().trim();

        if (!input.isEmpty()) {
            type = PropertyType.valueOf(input.toUpperCase());
        }

        System.out.println("Listing Types: SALE, RENT");
        System.out.print("Listing type (blank for any): ");
        input = sc.nextLine().trim();

        if (!input.isEmpty()) {
            listing = ListingType.valueOf(input.toUpperCase());
        }

        System.out.print("Minimum price (blank for any): ");
        input = sc.nextLine().trim();
        if (!input.isEmpty()) {
            min = new BigDecimal(input);
        }

        System.out.print("Maximum price (blank for any): ");
        input = sc.nextLine().trim();
        if (!input.isEmpty()) {
            max = new BigDecimal(input);
        }

        System.out.print("Minimum bedrooms (blank for any): ");
        input = sc.nextLine().trim();
        if (!input.isEmpty()) {
            bedrooms = Integer.parseInt(input);
        }

        List<Property> properties = propertyService.searchProperties(
                city, type, listing, min, max, bedrooms);

        System.out.println("\n           SEARCH RESULTS");
        System.out.println("----------------------------------------");

        if (properties.isEmpty()) {
            System.out.println("No matching properties found.");
            return;
        }

        for (Property p : properties) {
            displayProperty(p);
        }
    }

    private void viewProperty() throws Exception {
        System.out.print("\nEnter Property ID: ");
        int id = Integer.parseInt(sc.nextLine());

        Property p = propertyService.getPropertyById(id);
        displayProperty(p);
    }

    private void sendRequest() throws Exception {
        System.out.print("\nEnter Property ID: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Enter your message: ");
        String message = sc.nextLine();

        int requestId = requestService.createRequest(
                buyer.getUserId(), id, message);

        System.out.println("\nRequest submitted successfully!");
        System.out.println("Request ID: " + requestId);
    }

    private void viewRequests() throws Exception {
        List<PropertyRequest> requests =
                requestService.getRequestsByBuyer(buyer.getUserId());

        System.out.println("\n              MY REQUESTS");
        System.out.println("----------------------------------------");

        if (requests.isEmpty()) {
            System.out.println("You have no requests.");
            return;
        }

        for (PropertyRequest r : requests) {
            r.displayRequest();
            System.out.println("----------------------------------------");
        }
    }

    private void cancelRequest() throws Exception {
        System.out.print("\nEnter Request ID: ");
        int id = Integer.parseInt(sc.nextLine());

        requestService.cancelRequest(buyer.getUserId(), id);
        System.out.println("\nRequest cancelled successfully.");
    }

    private void purchaseProperty() throws Exception {
        System.out.print("\nEnter Property ID to purchase: ");
        int id = Integer.parseInt(sc.nextLine());

        int transactionId = transactionService.purchaseProperty(buyer, id);

        System.out.println("\nPurchase successful!");
        System.out.println("Transaction ID: " + transactionId);
    }

    private void transactionHistory() throws Exception {
        List<Transaction> transactions =
                transactionService.getBuyerTransactions(buyer.getUserId());

        System.out.println("\n          TRANSACTION HISTORY");
        System.out.println("----------------------------------------");

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Transaction t : transactions) {
            System.out.println("Transaction ID : " + t.getTransactionId());
            System.out.println("Property ID    : " + t.getPropertyId());
            System.out.println("Seller ID      : " + t.getSellerId());
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
        System.out.println("State       : " + p.getState());
        System.out.println("Price       : " + p.getPrice());
        System.out.println("Area        : " + p.getArea());
        System.out.println("Bedrooms    : " + p.getBedrooms());
        System.out.println("Bathrooms   : " + p.getBathrooms());
        System.out.println("Status      : " + p.getStatus());
        System.out.println("Description : " + p.getDescription());
        System.out.println("----------------------------------------");
    }
}
