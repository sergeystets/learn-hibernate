package learn.hibernate.services.impl;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import learn.hibernate.entity.User;
import learn.hibernate.exceptions.CustomRuntimeException;
import learn.hibernate.repository.UserRepository;
import learn.hibernate.services.IUserService;

/**
 * @author Sergii Stets
 *         Created: 01.02.2016
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
    public Optional<User> findUserById(BigInteger id) {
        return Optional.ofNullable(userRepository.findOne(id));
    }

    @Override
    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    @Transactional
    public void saveAndLog1(User user) {
        userRepository.save(user);
        log1(user);
    }

    @Override
    @Transactional(noRollbackFor = CustomRuntimeException.class)
    public void saveAndLog2(User user) {
        userRepository.save(user);
        log1(user);
    }

    @Transactional
    private void log1(User user) throws CustomRuntimeException {
        throw new CustomRuntimeException();
    }

}