package ir.maktabsharif.smspanel.service;

import ir.maktabsharif.smspanel.entity.Transaction;
import ir.maktabsharif.smspanel.entity.TransactionType;
import ir.maktabsharif.smspanel.entity.User;
import ir.maktabsharif.smspanel.entity.UserStatus;
import ir.maktabsharif.smspanel.exception.AuthenticationException;
import ir.maktabsharif.smspanel.exception.DomainException;
import ir.maktabsharif.smspanel.exception.DuplicateUsernameException;
import ir.maktabsharif.smspanel.exception.EntityNotFoundException;
import ir.maktabsharif.smspanel.exception.ValidationException;
import ir.maktabsharif.smspanel.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of every user related feature of the SMS panel.
 * All the business rules of the application live here; the console only collects
 * input from the user and prints what this class returns.
 */
public class UserService {

    /** Gift credit granted once, when the account is created. */
    public static final BigDecimal INITIAL_GIFT_CREDIT = new BigDecimal("5000.00");
    public static final int NAME_MIN_LENGTH = 3;
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int PASSWORD_MIN_LENGTH = 4;

    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public UserService(UserRepository userRepository, TransactionService transactionService) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository is required");
        this.transactionService = Objects.requireNonNull(transactionService, "transactionService is required");
    }

    // ------------------------------------------------------------------ 1. registration

    /**
     * Registers a new user and grants the initial gift credit.
     *
     * @return the freshly created account
     */
    public User register(String name, String username, String password) {
        String cleanName = requireText(name, "name", NAME_MIN_LENGTH);
        String cleanUsername = requireText(username, "username", USERNAME_MIN_LENGTH);
        String cleanPassword = requireText(password, "password", PASSWORD_MIN_LENGTH);

        if (cleanUsername.contains(" ")) {
            throw new ValidationException("username must not contain spaces");
        }
        if (userRepository.existsByUsername(cleanUsername)) {
            throw new DuplicateUsernameException("the username '" + cleanUsername + "' is already taken");
        }

        User user = new User(
                0,
                cleanName,
                cleanUsername,
                cleanPassword,
                INITIAL_GIFT_CREDIT,
                LocalDateTime.now(),
                UserStatus.ACTIVE
        );
        userRepository.save(user);

        transactionService.record(
                user.getId(),
                INITIAL_GIFT_CREDIT,
                TransactionType.GIFT,
                "Welcome gift credit for registering in the SMS panel"
        );
        return user;
    }

    // ------------------------------------------------------------------ 2. login

    /**
     * Authenticates a user.
     *
     * @return the authenticated account
     * @throws AuthenticationException when the credentials are wrong or the account is deactivated
     */
    public User login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            throw new ValidationException("username and password are required");
        }

        User user = userRepository.findByUsername(username.trim());
        if (user == null || !user.getPassword().equals(password)) {
            throw new AuthenticationException("the username or the password is incorrect");
        }
        if (!user.isActive()) {
            throw new AuthenticationException("this account has been deactivated and can no longer be used");
        }
        return user;
    }

    // ------------------------------------------------------------------ 3. increasing credit

    /**
     * Adds credit to an account and records the movement in its history.
     */
    public User increaseCredit(long userId, BigDecimal amount) {
        User user = getUserOrFail(userId);

        if (amount == null) {
            throw new ValidationException("the amount is required");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("the amount must be greater than zero");
        }
        if (!user.isActive()) {
            throw new DomainException("a deactivated account cannot receive credit");
        }

        BigDecimal rounded = amount.setScale(2, RoundingMode.HALF_UP);
        user.setCredit(user.getCredit().add(rounded));
        userRepository.update(user);

        transactionService.record(
                user.getId(),
                rounded,
                TransactionType.CREDIT_INCREASE,
                "Credit increase by the user"
        );
        return user;
    }

    // ------------------------------------------------------------------ 4. changing password

    /**
     * Changes the password after checking the current one.
     */
    public void changePassword(long userId, String currentPassword, String newPassword) {
        User user = getUserOrFail(userId);

        if (isBlank(currentPassword) || isBlank(newPassword)) {
            throw new ValidationException("the current password and the new password are required");
        }
        if (!user.getPassword().equals(currentPassword)) {
            throw new AuthenticationException("the current password is incorrect");
        }
        if (newPassword.length() < PASSWORD_MIN_LENGTH) {
            throw new ValidationException("the new password must be at least " + PASSWORD_MIN_LENGTH + " characters long");
        }
        if (newPassword.equals(currentPassword)) {
            throw new ValidationException("the new password must be different from the current password");
        }

        user.setPassword(newPassword);
        userRepository.update(user);
    }

    // ------------------------------------------------------------------ 5. viewing profile

    /**
     * @return the full account information of the given user
     */
    public User viewProfile(long userId) {
        return getUserOrFail(userId);
    }

    // ------------------------------------------------------------------ 6. transaction history

    /**
     * @return every movement recorded on the account of the given user, most recent first
     */
    public List<Transaction> viewTransactionHistory(long userId) {
        getUserOrFail(userId);
        return transactionService.viewHistory(userId);
    }

    // ------------------------------------------------------------------ 7. deactivating account

    /**
     * Deactivates an account. The record is kept, but the user can no longer log in.
     */
    public User deactivateAccount(long userId) {
        User user = getUserOrFail(userId);

        if (!user.isActive()) {
            throw new DomainException("this account is already deactivated");
        }

        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.update(user);
        return user;
    }

    // ------------------------------------------------------------------ helpers

    public List<User> findAll() {
        return userRepository.findAll();
    }

    private User getUserOrFail(long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new EntityNotFoundException("no account was found with id " + userId);
        }
        return user;
    }

    private static String requireText(String value, String fieldName, int minLength) {
        if (isBlank(value)) {
            throw new ValidationException("the " + fieldName + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.length() < minLength) {
            throw new ValidationException("the " + fieldName + " must be at least " + minLength + " characters long");
        }
        return trimmed;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
