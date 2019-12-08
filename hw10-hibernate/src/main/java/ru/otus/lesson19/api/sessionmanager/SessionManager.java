package ru.otus.lesson19.api.sessionmanager;

public interface SessionManager extends AutoCloseable {
    void beginSession();
    void commitSession();
    void rollbackSession();
    void close();

    DatabaseSession getCurrentSession();
}
