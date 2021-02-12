package ua.kiev.prog;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {
    Optional<T> get(int id);

    List<T> getByCondition(String condition);

    List<T> getAll();

    void insert(T t);

    void update(T t);

    void delete(int id);

    void create();

}
