package ir.maktabsharif.smspanel.repository;

import ir.maktabsharif.smspanel.entity.User;

/**
 * Persistence contract for {@link User}.
 */
public interface UserRepository extends GenericRepository<User, Long> {

    /**
     * @return the user owning the given username, or {@code null} when no account uses it
     */
    User findByUsername(String username);

    /**
     * @return {@code true} when an account already uses the given username
     */
    boolean existsByUsername(String username);
}
