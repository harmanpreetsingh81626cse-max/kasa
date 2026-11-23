import java.math.BigDecimal;
import java.time.LocalDateTime;
import main.com.enums.PropertyType;
import main.com.enums.ListingType;
import main.com.enums.PropertyStatus;

public class Property {
    private int propertyId;
    private int ownerId;
    private String title;
    private String description;
    private String location;
    private String city;
    private String state;
    private String pincode;
    private PropertyType propertyType;
    private ListingType listingType;

    private BigDecimal price;
    private BigDecimal area;

    private int bedrooms;
    private int bathrooms;
    private PropertyStatus status;
    private LocalDateTime createdAt;

    public Property(int propertyId, int ownerId, String title, String description, String location, String city,
            String state, String pincode, PropertyType propertyType, ListingType listingType, BigDecimal price,
            BigDecimal area, int bedrooms, int bathrooms, PropertyStatus status, LocalDateTime createdAt) {
        this.propertyId = propertyId;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.propertyType = propertyType;
        this.listingType = listingType;
        this.price = price;
        this.area = area;
        this.bathrooms = bathrooms;
        this.bathrooms = bathrooms;
        this.status = status;
        this.createdAt = createdAt;
    }
}
