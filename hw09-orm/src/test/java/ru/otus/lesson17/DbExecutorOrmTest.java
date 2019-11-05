package ru.otus.lesson17;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import ru.otus.lesson17.orm.DbExecutorOrm;
import ru.otus.lesson17.orm.DbExecutorOrmImpl;
import ru.otus.lesson17.model.Account;
import ru.otus.lesson17.model.User;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class DbExecutorOrmTest {

    private static final String URL = "jdbc:h2:mem:";

    private static void createTables(Connection connection) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement("create table user_table(id long identity primary key, name varchar(50), age int)")) {
            pst.executeUpdate();
        }
        try (PreparedStatement pst = connection.prepareStatement("create table account(no long identity primary key, type varchar(255), rest number(20, 2))")) {
            pst.executeUpdate();
        }
    }

    @Test
    void testDbExecutor() throws SQLException, IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        try (Connection connection = DriverManager.getConnection(URL)) {
            connection.setAutoCommit(false);
            createTables(connection);

            DbExecutorOrm<User> userOrm = new DbExecutorOrmImpl<>();

            // User
            User user = new User(null, "test", 5);
            Long id = (Long) userOrm.create(connection, user);
            Assert.assertEquals(Long.valueOf(1L), id);

            user.setAge(25);
            userOrm.update(connection, user);

            connection.commit();

            Optional<User> user1 = userOrm.load(connection, id, User.class);
            System.out.println(user1);
            Assert.assertEquals(user, user1.orElse(null));
            Assert.assertEquals(25, user1.get().getAge());

            // Account
            DbExecutorOrm<Account> accountOrm = new DbExecutorOrmImpl<>();
            Account account = new Account(null, "test", BigDecimal.valueOf(0));
            id = (Long) accountOrm.create(connection, account);
            Assert.assertEquals(Long.valueOf(1L), id);

            account.setRest(BigDecimal.valueOf(25.00));
            accountOrm.update(connection, account);

            connection.commit();

            Optional<Account> account1 = accountOrm.load(connection, id, Account.class);
            System.out.println(account1);
            Assert.assertEquals(account, account1.orElse(null));
            Assert.assertEquals(true, BigDecimal.valueOf(25.00).compareTo(account1.get().getRest()) == 0);

            account.setAccountType("test33");
            accountOrm.createOrUpdate(connection, account);
            final Account account2 = new Account(null, "test", BigDecimal.valueOf(500));
            accountOrm.createOrUpdate(connection, account2);
            Assert.assertNotNull(account2.getNo());
        }
    }

}
