package ir.maktabsharif.smspanel.repository;

import java.util.List;

/**
 * Minimal contract every repository of this project must fulfil.
 *
 * @param <T>  entity type handled by the repository
 * @param <ID> type of the entity identifier
 */
public interface GenericRepository<T, ID> {

    /**
     * Inserts a new entity. When the entity has no identifier yet, the generated one is assigned to it.
     *
     * @return the persisted entity (with its identifier filled in)
     */
    T save(T entity);

    /**
     * Updates an already persisted entity, matched by its identifier.
     *
     * @return the updated entity
     */
    T update(T entity);

    /**
     * @return the entity with the given identifier, or {@code null} when it does not exist
     */
    T findById(ID id);

    /**
     * @return every stored entity
     */
    List<T> findAll();
}
