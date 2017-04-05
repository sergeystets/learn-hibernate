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
    @Transactional
    public void saveAndLog(User user) {
        userRepository.save(user);
        log(user);
    }

    @Override
    @Transactional(noRollbackFor = CustomRuntimeException.class)
    public void saveAndLogNoRollBack(User user) {
        userRepository.save(user);
        log(user);
    }

    @Transactional
    private void log(User user) {
        throw new CustomRuntimeException();
    }
}