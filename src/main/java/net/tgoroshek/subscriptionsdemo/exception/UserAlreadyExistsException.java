package net.tgoroshek.subscriptionsdemo.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
    }

    public UserAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
