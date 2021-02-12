package ua.kiev.prog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DbProperties {
    private final String url;
    private final String user;
    private final String password;
    private final String initSQL;

    public DbProperties() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties");

        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        password = props.getProperty("db.password");
        initSQL = props.getProperty("db.initSQL");
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getInitSQL() {
        return initSQL;
    }
}
