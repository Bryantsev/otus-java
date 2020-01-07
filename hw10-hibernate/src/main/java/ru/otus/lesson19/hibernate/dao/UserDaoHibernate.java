package ru.otus.lesson19.hibernate.dao;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lesson19.api.dao.UserDao;
import ru.otus.lesson19.api.model.Phone;
import ru.otus.lesson19.api.model.User;
import ru.otus.lesson19.api.sessionmanager.SessionManager;
import ru.otus.lesson19.hibernate.sessionmanager.SessionManagerHibernate;

import java.util.List;
import java.util.Optional;

public class UserDaoHibernate implements UserDao {
    private static Logger logger = LoggerFactory.getLogger(UserDaoHibernate.class);

    private final SessionManagerHibernate sessionManager;

    public UserDaoHibernate(SessionManagerHibernate sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Optional<User> findById(long id, boolean loadAddress, boolean loadPhones) {
        final Session hibernateSession = sessionManager.getCurrentSession().getHibernateSession();
        final Optional<User> userOpt = hibernateSession.byId(User.class).loadOptional(id);
        userOpt.ifPresent(user -> {
            if (loadAddress) {
                Hibernate.initialize(user.getAddress());
            }
            if (loadPhones) {
                Hibernate.initialize(user.getPhones());
            }
        });
        return userOpt;
    }

    @Override
    public List<User> selectAll() {
        final Session hibernateSession = sessionManager.getCurrentSession().getHibernateSession();
        return hibernateSession.createNamedQuery("User.selectAll", User.class).getResultList();
    }

    @Override
    public User selectByName(String name) {
        final Session hibernateSession = sessionManager.getCurrentSession().getHibernateSession();
        final Query<User> queryUserByName = hibernateSession.createNamedQuery("User.selectByName", User.class);
        queryUserByName.setParameter("name", name);

        return queryUserByName.uniqueResult();
    }

    @Override
    public long saveUser(User user) {
        Session hibernateSession = sessionManager.getCurrentSession().getHibernateSession();
        if (user.getId() != null) {
            hibernateSession.merge(user);
        } else {
            hibernateSession.persist(user);
        }
        return user.getId();
    }

    @Override
    public boolean deleteUser(Long userId) {
        Session hibernateSession = sessionManager.getCurrentSession().getHibernateSession();
        if (userId != null) {
            // Загрузим пользователя и затем удалим
            User user = hibernateSession.load(User.class, userId);
            hibernateSession.delete(user);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean deletePhone(Phone phone) {
        Session hibernateSession = sessionManager.getCurrentSession().getHibernateSession();
        if (phone != null) {
            hibernateSession.delete(phone);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
