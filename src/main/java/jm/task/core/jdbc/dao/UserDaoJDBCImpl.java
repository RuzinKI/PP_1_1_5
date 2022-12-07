package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.UtilJDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {


    private static final String CREATE_USERS_TABLE = """
            CREATE TABLE IF NOT EXISTS user(
            id SERIAL,
            name TEXT,
            lastName TEXT,
            age INT);
            """;

    private static final String DROP_USERS_TABLE = """
            DROP TABLE IF EXISTS user;
            """;

    private static final String SAVE_USER = """
            INSERT INTO user (name, lastName, age)
            VALUE (?, ?, ?);
            """;

    private static final String REMOVE_USER_BY_ID = """
            DELETE FROM user 
            WHERE id = ?;
            """;

    private static final String GET_ALL_USERS = """
            SELECT *
            FROM user;
            """;

    private static final String CLEAN_USERS_TABLE = """
            DELETE FROM user 
            WHERE id IS NOT NULL;
            """;


    public UserDaoJDBCImpl() {

    }

    public void createUsersTable() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = UtilJDBC.get();
            statement = connection.createStatement();

            connection.setAutoCommit(false);
            statement.execute(CREATE_USERS_TABLE);
            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void dropUsersTable() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = UtilJDBC.get();
            statement = connection.createStatement();

            connection.setAutoCommit(false);
            statement.execute(DROP_USERS_TABLE);
            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void saveUser(String name, String lastName, byte age) throws SQLException {
        Connection connection = null;
        PreparedStatement prepareStatement = null;

        try  {
            connection = UtilJDBC.get();
            prepareStatement = connection.prepareStatement(SAVE_USER);

            connection.setAutoCommit(false);
            prepareStatement.setString(1, name);
            prepareStatement.setString(2, lastName);
            prepareStatement.setInt(3, age);
            boolean execute = prepareStatement.execute();
            connection.commit();

            if (!execute) {
                System.out.println("User с именем " + name + " добавлен в базу данных");
            }
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (prepareStatement != null) {
                prepareStatement.close();
            }
        }
    }

    public void removeUserById(long id) throws SQLException {
        Connection connection = null;
        PreparedStatement prepareStatement = null;

        try {
            connection = UtilJDBC.get();
            prepareStatement = connection.prepareStatement(REMOVE_USER_BY_ID);

            connection.setAutoCommit(false);
            prepareStatement.setLong(1, id);
            prepareStatement.execute();
            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (prepareStatement != null) {
                prepareStatement.close();
            }
        }
    }

    public List<User> getAllUsers() throws SQLException {
        Connection connection = null;
        PreparedStatement prepareStatement = null;
        List<User> users = new ArrayList<>();

        try {
            connection = UtilJDBC.get();
            prepareStatement = connection.prepareStatement(GET_ALL_USERS);

            connection.setAutoCommit(false);
            ResultSet resultSet = prepareStatement.executeQuery();

            while (resultSet.next()) {
                users.add(buildUsers(resultSet));
            }

            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (prepareStatement != null) {
                prepareStatement.close();
            }
            return users;
        }
    }

    public void cleanUsersTable() throws SQLException {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = UtilJDBC.get();
            statement = connection.createStatement();

            connection.setAutoCommit(false);
            statement.execute(CLEAN_USERS_TABLE);
            connection.commit();

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }

    private User buildUsers(ResultSet resultSet) throws SQLException {
        User user = new User(
                resultSet.getString("name"),
                resultSet.getString("lastName"),
                resultSet.getObject("age", Byte.class)
        );
        user.setId(resultSet.getLong("id"));
        return user;
    }
}
