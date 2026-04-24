package io.student.rcc.config;

public interface Config {

    static Config getInstance(){
        return LocalConfig.INSTANCE;
    }

    String frontUrl();
    String apiUrl();
    String apiJdbcUrl();
    String dbUsername();
    String dbPassword();
}
