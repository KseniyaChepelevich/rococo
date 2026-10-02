package io.student.rcc.config;

public interface Config {

    static Config getInstance() {

        return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.INSTANCE
        : LocalConfig.INSTANCE;
    }

    String frontUrl();
    String authJdbcUrl();
    String authUrl();
    String apiJdbcUrl();
    String apiUrl();
    String dbUsername();
    String dbPassword();

    default String oauthClientId() {
        return System.getProperty("oauth.client.id", "client");
    }

    default String oauthRedirectUri() {
        return System.getProperty("oauth.redirect.uri", "http://localhost:3000/authorized");
    }

    default String oauthScope() {
        return System.getProperty("oauth.scope", "openid profile");
    }
}
