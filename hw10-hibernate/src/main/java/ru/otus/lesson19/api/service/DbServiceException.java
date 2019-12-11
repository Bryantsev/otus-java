package ru.otus.lesson19.api.service;

public class DbServiceException extends RuntimeException {
  public DbServiceException(Exception e) {
    super(e);
  }
}
