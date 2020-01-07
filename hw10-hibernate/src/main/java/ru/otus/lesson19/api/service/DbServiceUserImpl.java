package ru.otus.lesson19.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lesson19.api.dao.UserDao;
import ru.otus.lesson19.api.model.User;
import ru.otus.lesson19.api.sessionmanager.SessionManager;
import ru.otus.lesson19.cache.HwCache;
import ru.otus.lesson19.cache.MyCache;

import java.util.List;
import java.util.Optional;

public class DbServiceUserImpl implements DBServiceUser {
    private static Logger logger = LoggerFactory.getLogger(DbServiceUserImpl.class);

    private UserDao userDao;
    private HwCache<Long, User> cache; // Кэш пользователей

    public DbServiceUserImpl(UserDao userDao) {
        initService(userDao, null);
    }

    /**
     * @param cache Кэш. Если null, то без кэша
     */
    public DbServiceUserImpl(UserDao userDao, HwCache<Long, User> cache) {
        initService(userDao, cache);
    }

    private void initService(UserDao userDao, HwCache<Long, User> cache) {
        this.userDao = userDao;
        if (cache != null) {
            this.cache = cache;
        } else {
            // Если кэш не задан, создадим заглушку
            this.cache = new MyCache<>("users", false);
        }
    }

    @Override
    public long saveUser(User user) {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            // Если есть пользователь с таким же именем при добавлении, то выкинем исключение
            if (user.getId() == null && userDao.selectByName(user.getName()) != null) {
                // Закроем сессию
                sessionManager.commitSession();
                throw new DbServiceException("Пользователь с именем " + user.getName() + " уже существует! Задайте уникальное имя, пожалуйста!");
            }
            long userId = userDao.saveUser(user);
            sessionManager.commitSession();
            cache.put(userId, user); // Добавим пользователя в кэш
            logger.debug("saved user: {}", userId);
            return userId;
        }
    }

    @Override
    public boolean deleteUser(Long userId) {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            boolean result = userDao.deleteUser(userId);
            sessionManager.commitSession();
            return result;
        }
    }

    @Override
    public Optional<User> getUser(long id, boolean loadAddress, boolean loadPhones) {

        final boolean useCache = loadAddress && loadPhones;
        if (useCache) {
            final User user = cache.get(id);
            // Если нашли значение в кэше, то сразу возвращаем его, иначе ищем в базе
            if (user != null) {
                logger.debug("User has gotten from cache: {}", user);
                return Optional.of(user);
            }
        }
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            Optional<User> userOptional = userDao.findById(id, loadAddress, loadPhones);
            // Сохраним найденного пользователя в кэш, если загружены все его данные
            if (useCache && userOptional.isPresent()) {
                cache.put(id, userOptional.get());
            }

            logger.debug("User has gotten from db: {}", userOptional.get());

            return userOptional;
        }
    }

    @Override
    public List<User> selectAll() {
        try (SessionManager sessionManager = userDao.getSessionManager()) {
            sessionManager.beginSession();
            return userDao.selectAll();
        }
    }

}
