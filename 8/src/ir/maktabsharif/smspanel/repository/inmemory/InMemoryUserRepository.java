package ir.maktabsharif.smspanel.repository.inmemory;

import ir.maktabsharif.smspanel.entity.User;
import ir.maktabsharif.smspanel.exception.EntityNotFoundException;
import ir.maktabsharif.smspanel.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory (HashMap based) implementation of {@link UserRepository}.
 * Useful for running the application without a database.
 */
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public User save(User user) {
        if (user.getId() == 0) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == 0 || !users.containsKey(user.getId())) {
            throw new EntityNotFoundException("user with id " + user.getId() + " does not exist");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(Long id) {
        if (id == null) {
            return null;
        }
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findByUsername(String username) {
        if (username == null) {
            return null;
        }
        for (User user : users.values()) {
            if (username.equalsIgnoreCase(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }
}
