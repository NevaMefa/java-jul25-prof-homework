package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;

public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    T instance = entityClassMetaData.getConstructor().newInstance();
                    for (Field field : entityClassMetaData.getAllFields()) {
                        field.setAccessible(true);
                        Object value = rs.getObject(field.getName());
                        field.set(instance, value);
                    }
                    return instance;
                }
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
                    List<T> result = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            T instance = entityClassMetaData.getConstructor().newInstance();
                            for (Field field : entityClassMetaData.getAllFields()) {
                                field.setAccessible(true);
                                Object value = rs.getObject(field.getName());
                                field.set(instance, value);
                            }
                            result.add(instance);
                        }
                        return result;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(Collections.emptyList());
    }

    @Override
    public long insert(Connection connection, T object) {
        var fields = entityClassMetaData.getFieldsWithoutId();
        var values = getFieldValues(object, fields);
        return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), values);
    }

    @Override
    public void update(Connection connection, T object) {
        var fields = entityClassMetaData.getFieldsWithoutId();
        var values = getFieldValues(object, fields);
        try {
            Field idField = entityClassMetaData.getIdField();
            idField.setAccessible(true);
            values.add(idField.get(object));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), values);
    }

    private List<Object> getFieldValues(T object, List<Field> fields) {
        return fields.stream()
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        return field.get(object);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
