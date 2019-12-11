package ru.otus.lesson19.api.service;

import ru.otus.lesson19.api.model.User;

import java.util.Optional;

public interface DBServiceUser {

    long saveUser(User user);

    Optional<User> getUser(long id, boolean loadAddress, boolean loadPhones);

}
