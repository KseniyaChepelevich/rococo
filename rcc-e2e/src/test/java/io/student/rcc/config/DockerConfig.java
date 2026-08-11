package io.student.rcc.config;

enum DockerConfig implements Config {
    INSTANCE;

    @Override
    public String frontUrl() {
        return "";
    }

    @Override
    public String authJdbcUrl() {
        return "";
    }

    @Override
    public String apiJdbcUrl() {
        return "";
    }

    @Override
    public String dbUsername() {
        return "root";
    }

    @Override
    public String dbPassword() {
        return "secret";
    }
}
