package learn.hibernate.repository.impl;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public class AbstractCrudRepository<T, ID extends Serializable> implements CrudRepository<T, ID> {

    @Override
    public <S extends T> S save(S entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        List<S> result = new ArrayList<>();
        for (S e : entities) {
            result.add(save(e));
        }
        return result;
    }

    @Override
    public T findOne(ID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exists(ID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<T> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(ID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(T entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }
}