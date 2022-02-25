package team.unnamed.inject.error;

/**
 * An exception that can be thrown while injecting
 * classes.
 */
public class InjectionException extends RuntimeException {

    public InjectionException() {
        super();
    }

    public InjectionException(String message) {
        super(message);
    }

    public InjectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InjectionException(Throwable cause) {
        super(cause);
    }

}
