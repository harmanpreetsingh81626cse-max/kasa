package main.com.exceptions;

public class PropertyNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public PropertyNotFoundException(int propertyId) {
        super("Property " + propertyId + " was not found");
    }
}
