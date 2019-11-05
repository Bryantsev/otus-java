package ru.otus.lesson17.orm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public interface DbExecutorOrm<T> {
    Object insertRecord(Connection connection, String sql, Collection<Object> params) throws SQLException;

    void updateRecord(Connection connection, String sql, Collection<Object> params) throws SQLException;

    Optional<T> selectRecord(Connection connection, String sql, long id, Function<ResultSet, T> rsHandler) throws SQLException;

    /**
     * Сохранить объект в базе
     *
     * @param connection Соединение
     * @param object     Объект
     * @return Ид-р сохраненного объекта
     */
    Object create(Connection connection, T object);

    void update(Connection connection, T object);

    void createOrUpdate(Connection connection, T object);

    Optional<T> load(Connection connection, Object id, Class<T> clazz);
}
