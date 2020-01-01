package ru.otus.lesson19;


import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lesson19.api.dao.UserDao;
import ru.otus.lesson19.api.model.Address;
import ru.otus.lesson19.api.model.Phone;
import ru.otus.lesson19.api.model.User;
import ru.otus.lesson19.api.service.DBServiceUser;
import ru.otus.lesson19.api.service.DbServiceUserImpl;
import ru.otus.lesson19.cache.HwCacheWithStats;
import ru.otus.lesson19.cache.MyCache;
import ru.otus.lesson19.hibernate.HibernateUtils;
import ru.otus.lesson19.hibernate.dao.UserDaoHibernate;
import ru.otus.lesson19.hibernate.sessionmanager.SessionManagerHibernate;

import java.util.Optional;

class UsersCacheTest {
    private static Logger logger = LoggerFactory.getLogger(UsersCacheTest.class);

    private static Object[] usersCountProvider() {
        return new Object[]{500, 3000, 7000};
    }

    static UserDao setupDb(long usersCount) {
        SessionFactory sessionFactory =
            HibernateUtils.buildSessionFactory("hibernate.cfg.xml", User.class, Address.class, Phone.class);

        SessionManagerHibernate sessionManager = new SessionManagerHibernate(sessionFactory);
        UserDao userDao = new UserDaoHibernate(sessionManager);

        DBServiceUser dbServiceUser = new DbServiceUserImpl(userDao); // без кэша
        // Добавляем пользователей
        for (long i = 1; i < usersCount + 1; i++) {
            dbServiceUser.saveUser(new User(i, "User" + i, 33));
        }
        return userDao;
    }

    @ParameterizedTest
    @MethodSource("usersCountProvider")
    void testReadingUsersWithoutCache(long usersCount) {
        // Создаем новый сервис без кэша
        final UserDao userDao = setupDb(usersCount);
        DBServiceUser dbServiceUser = new DbServiceUserImpl(userDao);

        logger.info("\n\n*** {} users without cache statistics ***", usersCount);
        long startTime = System.currentTimeMillis();
        // Читаем полные данные всех пользователей
        for (long i = 1; i < usersCount + 1; i++) {
            dbServiceUser.getUser(i, true, true);
        }
        long afterFirstReadingTime = System.currentTimeMillis();
        logger.info("Time to read all users first: {} ms", afterFirstReadingTime - startTime);

        // Повторно читаем полные данные всех пользователей с тестированием по имени
        for (long i = 1; i < usersCount + 1; i++) {
            Optional<User> user = dbServiceUser.getUser(i, true, true);
            Assert.assertEquals("User" + i, user.get().getName());
        }
        logger.info("Time to read all users the second one without cache: {} ms", System.currentTimeMillis() - afterFirstReadingTime);

        userDao.getSessionManager().close();
    }

    @ParameterizedTest
    @MethodSource("usersCountProvider")
    void testReadingUsersWithCache(long usersCount) {
        HwCacheWithStats<Long, User> cache = new HwCacheWithStats<>(new MyCache<>("users"));

        // Создаем новый сервис с кэшем
        final UserDao userDao = setupDb(usersCount);
        DBServiceUser dbServiceUser = new DbServiceUserImpl(userDao, cache);

        logger.info("\n\n*** {} users cache statistics ***", usersCount);
        long startTime = System.currentTimeMillis();
        // Читаем полные данные всех пользователей для прогрева кэша
        for (long i = 1; i < usersCount + 1; i++) {
            dbServiceUser.getUser(i, true, true);
        }
        long afterFirstReadingTime = System.currentTimeMillis();
        logger.info("Time to read all users first: {} ms", afterFirstReadingTime - startTime);

        // Сбросим статистику работы кэша, чтобы чтения значений на предыдущем шаге ее не искажали (попадания в кэш будут нулевыми, т.к. все значения уникальны)
        cache.resetStats();
        // Повторно читаем полные данные всех пользователей, с задействованием кэша с тестированием по имени.
        // При срабатывании мусорщика ранее добавленные значения будут из кэша удаляться
        for (long i = 1; i < usersCount + 1; i++) {
            Optional<User> user = dbServiceUser.getUser(i, true, true);
            Assert.assertEquals("User" + i, user.get().getName());
        }

        logger.info("Time to read all users the second one with cache: {} ms", System.currentTimeMillis() - afterFirstReadingTime);

        logger.info("cache.cacheRequests: {}", cache.getCacheRequests().getCount());
        logger.info("cache.cacheHits: {}", cache.getCacheHits().getCount());
        logger.info("cache.hitRatio: {}%", (double) Math.round(((double) cache.getCacheHits().getCount() / cache.getCacheRequests().getCount()) * 10000) / 100);

        userDao.getSessionManager().close();
    }

}
