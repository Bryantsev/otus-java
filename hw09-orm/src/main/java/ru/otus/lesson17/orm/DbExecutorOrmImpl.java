package ru.otus.lesson17.orm;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lesson17.orm.api.Column;
import ru.otus.lesson17.orm.api.Id;
import ru.otus.lesson17.orm.api.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.*;
import java.util.function.Function;

/**
 * @author sergey
 * created on 03.02.19.
 */
public class DbExecutorOrmImpl<T> implements DbExecutorOrm<T> {

    private static Logger logger = LoggerFactory.getLogger(DbExecutorOrmImpl.class);

    /**
     * Кэш информации о сущности класса T для маппинга на таблицу базы данных
     */
    private EntityInfo<T> entityInfoCache = null;

    @Override
    public Object insertRecord(Connection connection, String sql, Collection<Object> params) throws SQLException {
        Savepoint savePoint = connection.setSavepoint("savePointName");
        try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            final Object[] objects = params.toArray();
            for (int idx = 0; idx < objects.length; idx++) {
                // System.out.println(objects[idx]);
                pst.setObject(idx + 1, objects[idx]);
            }
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getObject(1);
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            connection.rollback(savePoint);
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void updateRecord(Connection connection, String sql, Collection<Object> params) throws SQLException {
        Savepoint savePoint = connection.setSavepoint("savePointName");
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            final Object[] objects = params.toArray();
            for (int idx = 0; idx < objects.length; idx++) {
                pst.setObject(idx + 1, objects[idx]);
            }
            pst.executeUpdate();

        } catch (SQLException ex) {
            connection.rollback(savePoint);
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public Optional<T> selectRecord(Connection connection, String sql, long id, Function<ResultSet, T> rsHandler) throws SQLException {
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setLong(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                return Optional.ofNullable(rsHandler.apply(rs));
            }
        }
    }

    /**
     * Сохранить объект в базе
     *
     * @param connection Соединение
     * @param object     Объект
     * @return Ид-р сохраненного объекта
     */
    @Override
    public Object create(Connection connection, T object) {
        final EntityInfo<T> entityInfo = getEntityInfo((Class<T>) object.getClass());
        Map<String, Object> values = entityInfo.getObjectValuesForSql(object);
        // Сгенерируем запрос
        final String sql =
            String.format
                (
                    "insert into %s (%s) values(%s)",
                    entityInfo.getTableName(),
                    String.join(",", values.keySet()),
                    StringUtils.stripEnd(Strings.repeat("?,", values.size()), ",")
                );
        logger.debug(sql);
        logger.debug("{} params: {}", entityInfo.getTableName(), values.values());
        try {
            Object id = insertRecord(connection, sql, values.values());

            // Установим объекту полученный ид-р, если получили значение
            Field idField = entityInfo.getIdField();
            logger.debug("{}: {}", idField == null ? "null" : idField.getName(), id);
            if (idField != null && id != null) {
                idField.set(object, id);
            }
            return id;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Connection connection, T object) {
        final EntityInfo<T> entityInfo = getEntityInfo((Class<T>) object.getClass());

        if (entityInfo.getIdField() == null) {
            throw new RuntimeException("Нельзя обновить объект без первичного ключа (@Id)!");
        }
        Map<String, Object> values = entityInfo.getObjectValuesForSql(object);
        final String idFieldName = entityInfo.getIdField().getName();
        if (values.get(idFieldName) == null) {
            throw new RuntimeException("Нельзя обновить объект со значением первичного ключа равным null!");
        }

        // Сгенерируем запрос
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(entityInfo.getTableName()).append(" set ");
        int fieldNum = 0;
        List<Object> params = new ArrayList<>(values.size()); // Параметры сохраним отдельно, т.к. у них будет отличаться порядок из-за поля ид-ра, которое будет добавлено в конце
        for (String field : values.keySet()) {
            fieldNum++;
            // Ид-р никогда не обновляем
            if (entityInfo.getIdField() != null && field.equals(idFieldName)) {
                continue;
            }
            sb.append(" ").append(field).append(" = ?");
            if (fieldNum < values.size()) {
                sb.append(",");
            }
            params.add(values.get(field));
        }
        sb.append(" where ").append(idFieldName).append(" = ?");
        params.add(values.get(idFieldName));

        final String sql = sb.toString();
        logger.debug(sql);
        logger.debug("{} params: {}", entityInfo.getTableName(), params);
        try {
            updateRecord(connection, sql, params);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void createOrUpdate(Connection connection, T object) {
        final EntityInfo<T> entityInfo = getEntityInfo((Class<T>) object.getClass());
        final Field idField = entityInfo.getIdField();
        if (idField == null) {
            throw new RuntimeException("Нельзя добавить или обновить объект без первичного ключа (@Id)!");
        }

        try {
            Object id = idField.get(object);
            // Если значение первичного ключа не задано, то однозначно добавляем новую запись
            if (id == null) {
                logger.debug("Значение первичного ключа не задано - создаем новую запись");
                create(connection, object);
            } else {
                // Попробуем загрузить запись по ид-ру. Если запись есть, то обновим ее, иначе добавим (просто ид-р был сгенерирован заранее)
                Optional<?> obj = load(connection, id, (Class<T>) object.getClass());
                if (obj.isPresent()) {
                    logger.debug("Значение первичного ключа задано и найдена запись в базе - обновляем ее");
                    update(connection, object);
                } else {
                    logger.debug("Значение первичного ключа задано, но запись не найдена в базе - добавляем новую запись");
                    create(connection, object);
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<T> load(Connection connection, Object id, Class<T> clazz) {
        if (id == null) {
            throw new RuntimeException("Нельзя загрузить объект по значению первичного ключа равному null!");
        }

        final EntityInfo<T> entityInfo = getEntityInfo(clazz);
        if (entityInfo.getIdField() == null) {
            throw new RuntimeException("Нельзя загрузить объект без поля первичного ключа (@Id)!");
        }
        final String idFieldName = entityInfo.getIdField().getName();

        // Сгенерируем запрос
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        int fieldNum = entityInfo.getFieldInfos().size();
        for (FieldInfo field : entityInfo.getFieldInfos()) {
            fieldNum--;
            sb.append(" ").append(field.getColumnName());
            if (fieldNum > 0) {
                sb.append(",");
            }
        }
        sb.append(" from ").append(entityInfo.getTableName());
        sb.append(" where ").append(idFieldName).append(" = ?");

        final String sql = sb.toString();
        logger.debug(sql);
        logger.debug("{}.{}: {}", entityInfo.getTableName(), idFieldName, id);

        T obj = null;
        try {
            try (PreparedStatement pst = connection.prepareStatement(sql)) {
                pst.setObject(1, id);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        obj = clazz.getDeclaredConstructor().newInstance();
                        for (FieldInfo field : entityInfo.getFieldInfos()) {
                            field.getaField().set(obj, rs.getObject(field.getColumnName()));
                        }
                    }
                }
            }

            return Optional.ofNullable(obj);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private EntityInfo<T> getEntityInfo(Class<T> clazz) {
        if (entityInfoCache == null) {
            logger.debug("EntityInfo for {} has added to cache!", clazz);
            entityInfoCache = new EntityInfo<>(clazz);
        } else {
            logger.debug("EntityInfo for {} has taken from cache!", clazz);
        }

        return entityInfoCache;
    }


    /**
     * Данные по маппингу сущности на таблицу в БД
     */
    private class EntityInfo<T> {
        private Class<T> clazz;
        private String tableName;
        private Table aTable;
        private List<FieldInfo> fieldInfos = new ArrayList<>();
        Field idField = null;

        public EntityInfo(Class<T> clazz) {
            this.clazz = clazz;
            aTable = this.clazz.getAnnotation(Table.class);
            if (aTable == null || Strings.isNullOrEmpty(aTable.value())) {
                tableName = this.clazz.getSimpleName();
            } else {
                tableName = aTable.value();
            }

            // Получим колонки таблицы/поля класса сущности
            Field[] fields = this.clazz.getDeclaredFields();
            for (Field field : fields) {
                // Игнорируем статические-поля
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                final FieldInfo fieldInfo = new FieldInfo(field);
                fieldInfos.add(fieldInfo);
                if (fieldInfo.isPrimary()) {
                    idField = field;
                }
            }
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public String getTableName() {
            return tableName;
        }

        public Table getaTable() {
            return aTable;
        }

        public List<FieldInfo> getFieldInfos() {
            return fieldInfos;
        }

        public Field getIdField() {
            return idField;
        }

        /**
         * Вернуть мапу полей строки таблицы со значениями по данным объекта
         *
         * @param o Объект с данными
         * @return Мапа
         */
        public Map<String, Object> getObjectValuesForSql(Object o) {
            Map<String, Object> values = new HashMap<>();
            try {
                for (FieldInfo fieldInfo : fieldInfos) {
                    // Сохраняем значение поля объекта
                    values.put(fieldInfo.getColumnName(), fieldInfo.getaField().get(o));
                }

                if (values.isEmpty()) {
                    throw new RuntimeException("Попытка сохранить в базу данных объект без полей!");
                }
                return values;

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class FieldInfo {
        private boolean primary;
        private Field aField;
        private Column aColumn;
        private String columnName;

        public FieldInfo(Field aField) {
            this.aField = aField;
            primary = aField.getAnnotation(Id.class) != null;
            aColumn = aField.getAnnotation(Column.class);
            columnName = aColumn == null || Strings.isNullOrEmpty(aColumn.value()) ? aField.getName() : aColumn.value();
        }

        public boolean isPrimary() {
            return primary;
        }

        public Field getaField() {
            return aField;
        }

        public String getFieldName() {
            return aField.getName();
        }

        public Column getaColumn() {
            return aColumn;
        }

        public String getColumnName() {
            return columnName;
        }
    }


}
