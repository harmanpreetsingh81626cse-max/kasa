import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;
import main.com.enums.UserRole;

public class Owner extends User {

    private boolean identityVerified;

    protected Owner(int id, String name, String email, String phone, String password, LocalDateTime date,
            boolean verified, UserRole role) {
        super(id, name, email, password, phone, date, role);

        this.identityVerified = verified;

    }

    // public void displayDashboard() {
    // System.out.println("Dashboard");
    // System.out.println("welcome" + " " + getUserName());
    // System.out.println("1. Add New Property");
    // System.out.println("2. View My Properties");
    // System.out.println("3. Update Property Details");
    // System.out.println("4. Delete Property Listing");
    // System.out.println("5. View Buyer/Tenant Requests");
    // System.out.println("6. Manage Requests");
    // System.out.println("7. View My Transactions");
    // System.out.println("8. View My Profile");
    // System.out.println("9. Update My Profile");
    // System.out.println("10. Change Password");
    // System.out.println("11. Logout");

    // Scanner sc = new Scanner(System.in);
    // System.out.print("Enter your choice : ");
    // int choice = sc.nextInt();

    // if (choice == 1) {
    // addProperty();
    // }else if (choice == 2) {
    // viewMyProperties();
    // }
    // else if (choice == 3) {
    // updateProperty();
    // }
    // else if (choice == 4) {
    // deleteProperty();
    // }
    // else if (choice == 5) {
    // viewBuyerRequests();
    // }
    // else if (choice == 6) {

    // }
    // }

    public boolean isIdentityVerified() {
        return identityVerified;
    }

    public void setIdentityVerified(boolean identityVerified) {
        this.identityVerified = identityVerified;

    }

    @Override
    public void displayProfile() {
        super.displayProfile();
        System.out.println("identity verified : " + identityVerified);
    }

}