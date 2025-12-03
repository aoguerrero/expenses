package onl.andres.expenses.core;

public enum AppParameters {

    INIT_DB("init_db", "false"),
    JDBC_CONNECTION("jdbc_connection", "jdbc:h2:mem:"),
    JDBC_USER("jdbc_user", "user"),
    JDBC_PASSWORD("jdbc_password", ""),
    APP_USERS("app_users", "user;secret");

    private final String name;
    private final String defaultValue;

    private AppParameters(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String get() {
        return System.getProperty(name, defaultValue);
    }

    public int getInt() {
        return Integer.valueOf(System.getProperty(name, defaultValue));
    }

    public boolean getBoolean() {
        return Boolean.valueOf(System.getProperty(name, defaultValue));
    }
}
