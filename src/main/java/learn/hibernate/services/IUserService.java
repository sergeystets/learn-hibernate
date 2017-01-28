package learn.hibernate.services;

import java.math.BigInteger;

import learn.hibernate.entity.User;

public interface IUserService {

    Iterable<User> listUsers();

    User findUserById(BigInteger id);
}
