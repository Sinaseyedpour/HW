package ir.maktabsharif.smspanel.repository;

import ir.maktabsharif.smspanel.entity.Transaction;

import java.util.List;

/**
 * Persistence contract for {@link Transaction}.
 */
public interface TransactionRepository extends GenericRepository<Transaction, Long> {

    /**
     * @return every transaction belonging to the given user, most recent first
     */
    List<Transaction> findByUserId(long userId);
}
