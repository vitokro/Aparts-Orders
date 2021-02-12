package ua.kiev.prog;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

public class AbstractDAO<T> implements DAO<T> {
    private final Connection conn;
    private final String table;
    private final Class<?> cls;

    public AbstractDAO(Connection conn, Class<?> cls) {
        this.conn = conn;
        this.table = cls.getSimpleName();
        this.cls = cls;
    }


    @Override
    public void insert(T t) {
        Field[] fields = t.getClass().getDeclaredFields();

        StringJoiner names = new StringJoiner(", ");
        StringJoiner values = new StringJoiner(", ");

        for (Field f : fields) {
            f.setAccessible(true);
            names.add(f.getName());
            try {
                values.add("'" + f.get(t) + "'");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        String sql = String.format("INSERT INTO %s(%s) VALUES(%s)", table, names.toString(), values.toString());

        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void update(T t) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            Field id = getFieldId(fields);
            StringBuilder sb = new StringBuilder();
            for (Field f : fields) {
                if (f != id) {
                    f.setAccessible(true);
                    sb.append(f.getName())
                            .append(" = '")
                            .append(f.get(t))
                            .append("',");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            String sql = String.format("UPDATE %s SET %s WHERE %s = %s",
                    table, sb.toString(), id.getName(), id.get(t));
            try (Statement st = conn.createStatement()) {
                st.execute(sql);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Field getFieldId(Field[] fields) {
        Field id = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No Id field"));
        id.setAccessible(true);
        return id;
    }

    @Override
    public void delete(int id) {
        Field[] fields = cls.getDeclaredFields();
        Field idField = getFieldId(fields);

        String sql = String.format("DELETE FROM %s WHERE %s = %s", table, idField.getName(), id);

        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public List<T> getAll() {
        List<T> res = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + table)) {
            while (rs.next()) {
                T t = initT(rs);
                res.add(t);
            }
        } catch (SQLException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException throwables) {
            throwables.printStackTrace();
        }

        return res;
    }

    private T initT(ResultSet rs) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, SQLException, NoSuchFieldException {
        final Constructor<?> constructor = cls.getConstructor();
        T t = (T) constructor.newInstance();
        ResultSetMetaData md = rs.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            String columnName = md.getColumnName(i);

            Field field = cls.getDeclaredField(columnName);
            field.setAccessible(true);

            field.set(t, rs.getObject(columnName));
        }
        return t;
    }

    @Override
    public Optional<T> get(int id) {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE id = " + id)) {
            if (rs.next()) {
                T t = initT(rs);
                return Optional.of(t);
            }
        } catch (SQLException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public List<T> getByCondition(String condition) {
        List<T> res = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE " + condition)) {
            while (rs.next()) {
                T t = initT(rs);
                res.add(t);
            }
        } catch (SQLException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public void create() {
        try {
            Statement st = conn.createStatement();
            try {
                Field[] fields = cls.getDeclaredFields();
                Field id = null;
                Set<Field> otherFields = new HashSet<>();
                for (Field f : fields) {
                    if (f.isAnnotationPresent(Id.class)) {
                        id = f;
                        id.setAccessible(true);
                    } else
                        otherFields.add(f);
                }
                if (id == null)
                    throw new RuntimeException("No Id field");
                StringBuilder sqlCreateTable = new StringBuilder();
                sqlCreateTable.append("CREATE TABLE ")
                        .append(table)
                        .append(" (")
                        .append(id.getName())
                        .append(" INT NOT NULL AUTO_INCREMENT PRIMARY KEY, ");

                for (Field field : otherFields) {
                    if (String.class == field.getType())
                        sqlCreateTable.append(field.getName()).append("  VARCHAR(20) NOT NULL, ");
                    if (int.class == field.getType())
                        sqlCreateTable.append(field.getName()).append("  INT,");
                }
                sqlCreateTable.append(")");
                sqlCreateTable.deleteCharAt(sqlCreateTable.length() - 2);
                st.execute("DROP TABLE IF EXISTS " + table);
                st.execute(sqlCreateTable.toString());
            } finally {
                st.close();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
