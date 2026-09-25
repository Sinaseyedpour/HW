package ir.maktabsharif.smspanel.exception;

/**
 * Raised when an entity referenced by the user cannot be found.
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
