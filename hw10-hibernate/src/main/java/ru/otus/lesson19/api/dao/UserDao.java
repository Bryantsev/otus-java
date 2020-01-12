package ru.otus.lesson19.api.dao;

import ru.otus.lesson19.api.model.Phone;
import ru.otus.lesson19.api.model.User;
import ru.otus.lesson19.api.sessionmanager.SessionManager;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(long id, boolean loadAddress, boolean loadPhones);

    List<User> selectAll();

    User selectByName(String name);

    long saveUser(User user);

    boolean deleteUser(Long userId);

    boolean deletePhone(Phone phone);

    SessionManager getSessionManager();

}
