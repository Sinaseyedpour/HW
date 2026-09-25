package ir.maktabsharif.smspanel.exception;

/**
 * Base class for every business (domain) error raised by the service layer.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
