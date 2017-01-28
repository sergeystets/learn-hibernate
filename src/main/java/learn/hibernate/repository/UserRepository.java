package learn.hibernate.repository;

import java.math.BigInteger;

import org.springframework.data.repository.CrudRepository;

import learn.hibernate.entity.User;

/**
 @author Sergii Stets
 Created: 01.02.2016
 **/
public interface UserRepository extends CrudRepository<User, BigInteger> {

}
