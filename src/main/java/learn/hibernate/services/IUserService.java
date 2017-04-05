package learn.hibernate.services;

import java.math.BigInteger;
import java.util.Optional;

import learn.hibernate.entity.User;

public interface IUserService {

    Iterable<User> listUsers();

    Optional<User> findUserById(BigInteger id);

    void saveAndLog(User user);

    void saveAndLogNoRollBack(User user);
}
