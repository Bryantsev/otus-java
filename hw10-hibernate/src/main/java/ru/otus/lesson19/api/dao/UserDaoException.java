package ru.otus.lesson19.api.dao;

public class UserDaoException extends RuntimeException {
  public UserDaoException(Exception ex) {
    super(ex);
  }
}
