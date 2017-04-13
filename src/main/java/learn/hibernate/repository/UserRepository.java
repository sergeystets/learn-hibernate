package learn.hibernate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import learn.hibernate.entity.User;

/**
 * @author Sergii Stets
 *         Created: 01.02.2016
 **/
public interface UserRepository extends CrudRepository<User, Long> {

    @Modifying
    @Query("update User u set u.name = :name where u.id = :id")
    int updateUserName(@Param("name") String name, @Param("id") Long id);

    List<User> findAllByName(String name);
}
