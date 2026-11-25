package main.com.exceptions;

public class UnauthorizedOperationException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
