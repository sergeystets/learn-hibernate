package learn.hibernate;


import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

public final class SqlSupport {

    public interface User {
        String FIND_ONE = "SELECT * FROM user WHERE user.id = :id";
        String FIND_ALL_BY_NAME = "SELECT * FROM user WHERE user.name = :name";
        String UPDATE_NAME = "UPDATE user SET name = :name WHERE id = :id";
        String INSERT_USER = "INSERT INTO user (name) VALUES (:name)";
        String INSERT_USER_WITH_ID = "INSERT INTO user (id, name) VALUES (:id, :name)";

        RowMapper<learn.hibernate.entity.User> mapper =
                BeanPropertyRowMapper.newInstance(learn.hibernate.entity.User.class);

    }
}
