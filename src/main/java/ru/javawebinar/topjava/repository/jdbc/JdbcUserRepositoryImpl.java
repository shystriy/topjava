package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiFunction;

@Repository
public class JdbcUserRepositoryImpl implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final ResultSetExtractor<List<User>> resultSetExtractor = new ResultSetExtractor<List<User>>() {
        @Override
        public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Integer, User> map = new HashMap<>();
            while (rs.next()) {
                if (map.containsKey(rs.getInt("id"))) {
                    map.get(rs.getInt("id")).getRoles().add(Role.valueOf(rs.getString("roles")));
                    continue;
                }

                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("pas"),
                        rs.getInt("calories"),
                        rs.getBoolean("enabled"),
                        rs.getDate("registered"),
                        rs.getString("roles") == null ? Collections.emptySet() : Collections.singletonList(Role.valueOf(rs.getString("roles"))
                        ));

                map.put(user.getId(), user);
            }

            return new ArrayList<>(map.values());
        }
    };
            /*JdbcTemplateMapperFactory
                    .newInstance()
                    .addKeys("id") // the column name you expect the user id to be on
                    .newResultSetExtractor(User.class);*/

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final SimpleJdbcInsert insertRole;

    @Autowired
    public JdbcUserRepositoryImpl(DataSource dataSource, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(dataSource)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        this.insertRole = new SimpleJdbcInsert(dataSource)
                .withTableName("user_roles")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            for (Role role : user.getRoles()) {
                jdbcTemplate.update("INSERT INTO user_roles (user_id, role) VALUES (?, ?)", user.getId(), role.name());
            }

        } else if (namedParameterJdbcTemplate.update(
                "UPDATE users SET name=:name, email=:email, password=:password, " +
                        "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", parameterSource) == 0) {
            return null;
        } else {
            jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
            for (Role role : user.getRoles()) {
                jdbcTemplate.update("INSERT INTO user_roles (user_id, role) VALUES (?, ?)", user.getId(), role.name());
            }
        }
        return user;
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT " +
                "U.id AS \"id\", " +
                "U.password AS \"pas\", " +
                "U.calories_per_day AS \"calories\", " +
                "U.registered AS \"registered\", " +
                "U.name AS \"name\", " +
                "U.email AS \"email\", " +
                "U.enabled AS \"enabled\", " +
                "UR.role AS \"roles\" " +
                "FROM users U LEFT JOIN user_roles UR ON U.id=UR.user_id " +
                "WHERE U.id=?", resultSetExtractor, id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT " +
                "U.id AS \"id\", " +
                "U.password AS \"pas\", " +
                "U.calories_per_day AS \"calories\", " +
                "U.registered AS \"registered\", " +
                "U.name AS \"name\", " +
                "U.email AS \"email\", " +
                "U.enabled AS \"enabled\", " +
                "UR.role AS \"roles\" " +
                "FROM users U LEFT JOIN user_roles UR ON U.id=UR.user_id " +
                "WHERE email=?", resultSetExtractor, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT " +
                "U.id AS \"id\", " +
                "U.password AS \"pas\", " +
                "U.calories_per_day AS \"calories\", " +
                "U.registered AS \"registered\", " +
                "U.name AS \"name\", " +
                "U.email AS \"email\", " +
                "U.enabled AS \"enabled\", " +
                "UR.role AS \"roles\" " +
                "FROM users U LEFT JOIN user_roles UR ON U.id=UR.user_id ORDER BY U.name, U.email", resultSetExtractor);
    }
}
