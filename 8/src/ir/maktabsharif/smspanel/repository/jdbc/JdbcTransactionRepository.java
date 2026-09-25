package ir.maktabsharif.smspanel.repository.jdbc;

import ir.maktabsharif.smspanel.entity.Transaction;
import ir.maktabsharif.smspanel.entity.TransactionType;
import ir.maktabsharif.smspanel.exception.DomainException;
import ir.maktabsharif.smspanel.repository.DatabaseConnection;
import ir.maktabsharif.smspanel.repository.TransactionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL/JDBC implementation of {@link TransactionRepository}.
 */
public class JdbcTransactionRepository implements TransactionRepository {

    private static final String INSERT =
            "INSERT INTO transactions (user_id, amount, type, description, transaction_date) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE transactions SET user_id = ?, amount = ?, type = ?, description = ?, transaction_date = ? WHERE id = ?";
    private static final String SELECT_ALL =
            "SELECT id, user_id, amount, type, description, transaction_date FROM transactions";
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id = ?";
    private static final String SELECT_BY_USER = SELECT_ALL + " WHERE user_id = ? ORDER BY transaction_date DESC, id DESC";

    @Override
    public Transaction save(Transaction transaction) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, transaction.getUserId());
            statement.setBigDecimal(2, transaction.getAmount());
            statement.setString(3, transaction.getType().name());
            statement.setString(4, transaction.getDescription());
            statement.setTimestamp(5, Timestamp.valueOf(transaction.getDate()));

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    transaction.setId(keys.getLong(1));
                }
            }
            return transaction;
        } catch (SQLException e) {
            throw new DomainException("could not save the transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public Transaction update(Transaction transaction) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {

            statement.setLong(1, transaction.getUserId());
            statement.setBigDecimal(2, transaction.getAmount());
            statement.setString(3, transaction.getType().name());
            statement.setString(4, transaction.getDescription());
            statement.setTimestamp(5, Timestamp.valueOf(transaction.getDate()));
            statement.setLong(6, transaction.getId());

            statement.executeUpdate();
            return transaction;
        } catch (SQLException e) {
            throw new DomainException("could not update the transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public Transaction findById(Long id) {
        if (id == null) {
            return null;
        }
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {

            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? map(result) : null;
            }
        } catch (SQLException e) {
            throw new DomainException("could not read the transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL + " ORDER BY id");
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                transactions.add(map(result));
            }
            return transactions;
        } catch (SQLException e) {
            throw new DomainException("could not read the transactions: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Transaction> findByUserId(long userId) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_USER)) {

            statement.setLong(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    transactions.add(map(result));
                }
            }
            return transactions;
        } catch (SQLException e) {
            throw new DomainException("could not read the transactions: " + e.getMessage(), e);
        }
    }

    private Transaction map(ResultSet result) throws SQLException {
        return new Transaction(
                result.getLong("id"),
                result.getLong("user_id"),
                result.getBigDecimal("amount"),
                TransactionType.valueOf(result.getString("type")),
                result.getString("description"),
                result.getTimestamp("transaction_date").toLocalDateTime()
        );
    }
}
