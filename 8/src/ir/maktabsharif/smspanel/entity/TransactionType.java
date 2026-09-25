package ir.maktabsharif.smspanel.entity;

/**
 * Kind of movement applied to a user's credit.
 */
public enum TransactionType {
    /** Credit granted by the system when the account is created. */
    GIFT,
    /** Credit purchased by the user. */
    CREDIT_INCREASE,
    /** Credit consumed by the panel (reserved for future features such as sending SMS). */
    DEBIT
}
