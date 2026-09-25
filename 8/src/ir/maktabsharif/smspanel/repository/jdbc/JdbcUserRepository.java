package ir.maktabsharif.smspanel.repository.jdbc;

import ir.maktabsharif.smspanel.entity.User;
import ir.maktabsharif.smspanel.entity.UserStatus;
import ir.maktabsharif.smspanel.exception.DomainException;
import ir.maktabsharif.smspanel.repository.DatabaseConnection;
import ir.maktabsharif.smspanel.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL/JDBC implementation of {@link UserRepository}.
 */
public class JdbcUserRepository implements UserRepository {

    private static final String INSERT =
            "INSERT INTO users (name, username, password, credit, registration_date, status) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE users SET name = ?, username = ?, password = ?, credit = ?, registration_date = ?, status = ? WHERE id = ?";
    private static final String SELECT_ALL =
            "SELECT id, name, username, password, credit, registration_date, status FROM users";
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id = ?";
    private static final String SELECT_BY_USERNAME = SELECT_ALL + " WHERE username = ?";

    @Override
    public User save(User user) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setBigDecimal(4, user.getCredit());
            statement.setTimestamp(5, Timestamp.valueOf(user.getRegistrationDate()));
            statement.setString(6, user.getStatus().name());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new DomainException("could not save the user: " + e.getMessage(), e);
        }
    }

    @Override
    public User update(User user) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setBigDecimal(4, user.getCredit());
            statement.setTimestamp(5, Timestamp.valueOf(user.getRegistrationDate()));
            statement.setString(6, user.getStatus().name());
            statement.setLong(7, user.getId());

            statement.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new DomainException("could not update the user: " + e.getMessage(), e);
        }
    }

    @Override
    public User findById(Long id) {
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
            throw new DomainException("could not read the user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL + " ORDER BY id");
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                users.add(map(result));
            }
            return users;
        } catch (SQLException e) {
            throw new DomainException("could not read the users: " + e.getMessage(), e);
        }
    }

    @Override
    public User findByUsername(String username) {
        if (username == null) {
            return null;
        }
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_USERNAME)) {

            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? map(result) : null;
            }
        } catch (SQLException e) {
            throw new DomainException("could not read the user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }

    private User map(ResultSet result) throws SQLException {
        return new User(
                result.getLong("id"),
                result.getString("name"),
                result.getString("username"),
                result.getString("password"),
                result.getBigDecimal("credit"),
                result.getTimestamp("registration_date").toLocalDateTime(),
                UserStatus.valueOf(result.getString("status"))
        );
    }
}
