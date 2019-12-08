package ru.otus.lesson19.api.dao;

import ru.otus.lesson19.api.model.Phone;
import ru.otus.lesson19.api.model.User;
import ru.otus.lesson19.api.sessionmanager.SessionManager;

import java.util.Optional;

public interface UserDao {

    Optional<User> findById(long id);

    long saveUser(User user);

    boolean deletePhone(Phone phone);

    SessionManager getSessionManager();

}
