package ir.maktabsharif.smspanel.exception;

/**
 * Raised when a user cannot be authenticated (wrong credentials or deactivated account).
 */
public class AuthenticationException extends DomainException {

    public AuthenticationException(String message) {
        super(message);
    }
}
