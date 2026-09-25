package ir.maktabsharif.smspanel.exception;

/**
 * Raised when the data supplied by the user does not respect the business rules.
 */
public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(message);
    }
}
