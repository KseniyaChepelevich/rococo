package io.student.rcc.config;

public interface Config {

    static Config getInstance(){
        return LocalConfig.INSTANCE;
    }

    String frontUrl();
    String authJdbcUrl();
    String dbUsername();
    String dbPassword();
}
