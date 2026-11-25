package main.com.exceptions;

public class InvalidCredentialsException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
