package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final EntityClassMetaData<?> meta;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> meta) {
        this.meta = meta;
    }

    @Override
    public String getSelectAllSql() {
        return "SELECT * FROM " + meta.getName();
    }

    @Override
    public String getSelectByIdSql() {
        return getSelectAllSql() + " WHERE " + meta.getIdField().getName() + " = ?";
    }

    @Override
    public String getInsertSql() {
        var fields = meta.getFieldsWithoutId();
        var columnNames = fields.stream().map(Field::getName).collect(Collectors.joining(", "));
        var placeholders = fields.stream().map(f -> "?").collect(Collectors.joining(", "));
        return "INSERT INTO " + meta.getName() + " (" + columnNames + ") VALUES (" + placeholders + ")";
    }

    @Override
    public String getUpdateSql() {
        var fields = meta.getFieldsWithoutId();
        var setClause = fields.stream().map(f -> f.getName() + " = ?").collect(Collectors.joining(", "));
        return "UPDATE " + meta.getName() + " SET "
                + setClause + " WHERE "
                + meta.getIdField().getName() + " = ?";
    }
}
