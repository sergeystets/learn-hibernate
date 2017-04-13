package learn.hibernate.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import learn.hibernate.entity.User;
import learn.hibernate.repository.UserRepository;

@Repository("user-jdbc-repository")
public class UserJdbcRepository extends AbstractCrudRepository<User, Long> implements UserRepository {

    private static final String FIND_ONE = "SELECT * FROM user WHERE user.id = :id";
    private static final String FIND_ALL_BY_NAME = "SELECT * FROM user WHERE user.name = :name";
    private static final String UPDATE_NAME = "UPDATE user SET name = :name WHERE id = :id";
    private static final String INSERT_USER = "INSERT INTO user (id, name) VALUES (:id, :name)";

    private static final RowMapper<User> mapper = BeanPropertyRowMapper.newInstance(learn.hibernate.entity.User.class);

    private NamedParameterJdbcTemplate jdbc;

    public UserJdbcRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public int updateUserName(String name, Long id) {
        return jdbc.update(UPDATE_NAME, idAndName(id, name));
    }

    @Override
    public List<User> findAllByName(String name) {
        return jdbc.query(FIND_ALL_BY_NAME, name(name), mapper);
    }

    @Override
    public <S extends User> S save(S entity) {
        KeyHolder idHolder = new GeneratedKeyHolder();
        jdbc.update(INSERT_USER, new BeanPropertySqlParameterSource(entity), idHolder);
        Optional<Number> id = Optional.ofNullable(idHolder.getKey());
        entity.setId(id.map(Number::longValue).orElse(null));

        return entity;
    }

    @Override
    public User findOne(Long id) {
        return jdbc.queryForObject(FIND_ONE, id(id), mapper);
    }

    private static MapSqlParameterSource id(Long id) {
        return new MapSqlParameterSource("id", id);
    }

    private static MapSqlParameterSource name(String name) {
        return new MapSqlParameterSource("name", name);
    }

    private static MapSqlParameterSource idAndName(Long id, String name) {
        return new MapSqlParameterSource("id", id).addValue("name", name);
    }
}
