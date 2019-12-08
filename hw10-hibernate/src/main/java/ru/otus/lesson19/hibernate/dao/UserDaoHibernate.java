package ru.otus.lesson19.hibernate.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lesson19.api.dao.UserDao;
import ru.otus.lesson19.api.dao.UserDaoException;
import ru.otus.lesson19.api.model.Phone;
import ru.otus.lesson19.api.model.User;
import ru.otus.lesson19.api.sessionmanager.SessionManager;
import ru.otus.lesson19.hibernate.sessionmanager.DatabaseSessionHibernate;
import ru.otus.lesson19.hibernate.sessionmanager.SessionManagerHibernate;

import java.util.Optional;

public class UserDaoHibernate implements UserDao {
    private static Logger logger = LoggerFactory.getLogger(UserDaoHibernate.class);

    private final SessionManagerHibernate sessionManager;

    public UserDaoHibernate(SessionManagerHibernate sessionManager) {
        this.sessionManager = sessionManager;
    }


    @Override
    public Optional<User> findById(long id) {
        try {
            return sessionManager.getCurrentSession().getHibernateSession().byId(User.class).loadOptional(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.empty();
    }


    @Override
    public long saveUser(User user) {
        try {
            Session hibernateSession = sessionManager.getCurrentSession().getHibernateSession();
            if (user.getId() != null) {
                hibernateSession.merge(user);
            } else {
                hibernateSession.persist(user);
            }
            return user.getId();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UserDaoException(e);
        }
    }

    @Override
    public boolean deletePhone(Phone phone) {
        try {
            Session hibernateSession = sessionManager.getCurrentSession().getHibernateSession();
            if (phone != null) {
                hibernateSession.delete(phone);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UserDaoException(e);
        }
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
