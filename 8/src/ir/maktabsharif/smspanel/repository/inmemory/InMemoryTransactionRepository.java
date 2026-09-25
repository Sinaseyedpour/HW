package ir.maktabsharif.smspanel.repository.inmemory;

import ir.maktabsharif.smspanel.entity.Transaction;
import ir.maktabsharif.smspanel.exception.EntityNotFoundException;
import ir.maktabsharif.smspanel.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory (HashMap based) implementation of {@link TransactionRepository}.
 */
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Map<Long, Transaction> transactions = new HashMap<>();
    private long nextId = 1;

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == 0) {
            transaction.setId(nextId++);
        }
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public Transaction update(Transaction transaction) {
        if (transaction.getId() == 0 || !transactions.containsKey(transaction.getId())) {
            throw new EntityNotFoundException("transaction with id " + transaction.getId() + " does not exist");
        }
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public Transaction findById(Long id) {
        if (id == null) {
            return null;
        }
        return transactions.get(id);
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions.values());
    }

    @Override
    public List<Transaction> findByUserId(long userId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions.values()) {
            if (transaction.getUserId() == userId) {
                result.add(transaction);
            }
        }
        result.sort(Comparator.comparing(Transaction::getDate).reversed());
        return result;
    }
}
