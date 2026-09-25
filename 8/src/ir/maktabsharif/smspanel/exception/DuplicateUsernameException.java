package ir.maktabsharif.smspanel.exception;

/**
 * Raised on registration when the requested username is already taken.
 */
public class DuplicateUsernameException extends DomainException {

    public DuplicateUsernameException(String message) {
        super(message);
    }
}
