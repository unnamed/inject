package me.yushust.inject.error;

public class FactoryException extends RuntimeException {

    public FactoryException() {
    }

    public FactoryException(String message) {
        super(message);
    }

    public FactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public FactoryException(Throwable cause) {
        super(cause);
    }

}
