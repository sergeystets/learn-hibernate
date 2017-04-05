package learn.hibernate.services;

import java.math.BigInteger;
import java.util.Optional;

import learn.hibernate.entity.User;

public interface IUserService {

    Iterable<User> listUsers();

    Optional<User> findUserById(BigInteger id);

    Optional<User> findByName(String name);

    void saveAndLog1(User user);

    void saveAndLog2(User user);
}
