package learn.hibernate.services.impl;

import java.math.BigInteger;

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

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Iterable<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(BigInteger id) {
        return userRepository.findOne(id);
    }
}