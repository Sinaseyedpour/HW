package ir.maktabsharif.smspanel.service;

import ir.maktabsharif.smspanel.entity.Transaction;
import ir.maktabsharif.smspanel.entity.TransactionType;
import ir.maktabsharif.smspanel.exception.ValidationException;
import ir.maktabsharif.smspanel.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Business logic around the credit movements of the panel accounts.
 */
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = Objects.requireNonNull(transactionRepository, "transactionRepository is required");
    }

    /**
     * Persists a new movement of credit for the given user.
     *
     * @param userId      owner of the account
     * @param amount      signed amount of the movement
     * @param type        nature of the movement
     * @param description free text shown to the user in the history
     */
    public Transaction record(long userId, BigDecimal amount, TransactionType type, String description) {
        if (userId <= 0) {
            throw new ValidationException("a transaction must belong to an existing user");
        }
        if (amount == null) {
            throw new ValidationException("transaction amount is required");
        }
        if (type == null) {
            throw new ValidationException("transaction type is required");
        }

        Transaction transaction = new Transaction(
                0,
                userId,
                amount.setScale(2, RoundingMode.HALF_UP),
                type,
                description,
                LocalDateTime.now()
        );
        return transactionRepository.save(transaction);
    }

    /**
     * @return every transaction of the given user, most recent first
     */
    public List<Transaction> viewHistory(long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction findById(long id) {
        return transactionRepository.findById(id);
    }
}
