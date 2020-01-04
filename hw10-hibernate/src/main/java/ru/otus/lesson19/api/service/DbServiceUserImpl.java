package ru.otus.lesson19.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lesson19.api.dao.UserDao;
import ru.otus.lesson19.api.model.User;
import ru.otus.lesson19.api.sessionmanager.SessionManager;
import ru.otus.lesson19.cache.HwCache;
import ru.otus.lesson19.cache.MyCache;

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
            try {
                long userId = userDao.saveUser(user);
                sessionManager.commitSession();
                cache.remove(userId); // Доп-но удалим пользователя из кэша
                logger.debug("saved user: {}", userId);
                return userId;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
                throw new DbServiceException(e);
            }
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
            try {
                Optional<User> userOptional = userDao.findById(id, loadAddress, loadPhones);
                // Сохраним найденного пользователя в кэш, если загружены все его данные
                if (useCache && userOptional.isPresent()) {
                    cache.put(id, userOptional.get());
                }

                logger.debug("User has gotten from db: {}", userOptional.orElse(null));

                return userOptional;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                sessionManager.rollbackSession();
            }
            return Optional.empty();
        }
    }
}
