import java.time.LocalDate;
import java.time.LocalDateTime;

public class Owner extends User {
    private int totalListings;
    private boolean verified;

    Owner(int id, String name, String email, String phone, String password, LocalDateTime date, int totallisting,
            boolean verified) {
        super(id, name, email, password, phone, date);
        this.totalListings = totallisting;
        this.verified = verified;

    }

    public void displayProfile() {

    }

}