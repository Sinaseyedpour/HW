package ir.maktabsharif.smspanel.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A single movement of credit on a user's account.
 */
public class Transaction {

    private long id;
    private long userId;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private LocalDateTime date;

    public Transaction() {
    }

    public Transaction(long id, long userId, BigDecimal amount,
                       TransactionType type, String description, LocalDateTime date) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
