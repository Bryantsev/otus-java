package ru.otus.lesson19.api.service;

import ru.otus.lesson19.api.model.User;

import java.util.List;
import java.util.Optional;

public interface DBServiceUser {

    long saveUser(User user);

    boolean deleteUser(Long userId);

    Optional<User> getUser(long id, boolean loadAddress, boolean loadPhones);

    List<User> selectAll();

}
