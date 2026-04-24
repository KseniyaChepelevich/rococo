package io.student.rcc.config;

enum LocalConfig implements Config{
    INSTANCE;

    @Override
    public String frontUrl() {
        return "http://localhost:3000";
    }

    @Override
    public String apiUrl() {
        return "http://localhost:8080";
    }

    @Override
    public String apiJdbcUrl() {
        return "jdbc:mysql://localhost:3306/rococo-api";
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
