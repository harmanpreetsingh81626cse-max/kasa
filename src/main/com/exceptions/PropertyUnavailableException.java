package main.com.exceptions;

import main.com.enums.PropertyStatus;

public class PropertyUnavailableException extends Exception {
    private static final long serialVersionUID = 1L;


    public PropertyUnavailableException(int propertyId, PropertyStatus status) {
        super("Property " + propertyId + " is not available (" + status + ")");
    }

    public PropertyUnavailableException(String message) {
        super(message);
    }
}
