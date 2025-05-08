package net.tgoroshek.subscriptionsdemo.exception;

public class InvalidDataException extends RuntimeException{
    public InvalidDataException(Throwable cause) {
        super(cause);
    }

    public InvalidDataException(String message) {
        super(message);
    }
}
