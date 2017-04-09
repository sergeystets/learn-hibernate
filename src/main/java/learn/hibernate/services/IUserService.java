package learn.hibernate.services;

import java.math.BigInteger;
import java.util.Optional;

import learn.hibernate.entity.User;

public interface IUserService {

    Iterable<User> listUsers();

    Optional<User> findUserById(Long id);

    void rollbackSave(User user);

    void noRollbackSave(User user);
}
