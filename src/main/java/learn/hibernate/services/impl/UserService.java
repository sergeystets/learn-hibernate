package learn.hibernate.services.impl;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import learn.hibernate.entity.User;
import learn.hibernate.repository.UserRepository;
import learn.hibernate.services.IUserService;

/**
 @author Sergii Stets
 Created: 01.02.2016
 **/
@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository repository;

    @Override
    public Iterable<User> listUsers() {
        return repository.findAll();
    }

    @Override
    public User findUserById(BigInteger id) {
        return repository.findOne(id);
    }
}