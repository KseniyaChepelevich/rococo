package io.student.rcc.data.mapper.jpa;

import io.student.rcc.config.Config;
import io.student.rcc.data.mapper.tpl.DataSources;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManagers {

    private static final Config CFG = Config.getInstance();
    private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<>();

    private EntityManagers() {
    }

    @SuppressWarnings("resource")
    public static EntityManager em(String jdbcUrl) {
        EntityManagerFactory emf = emfs.computeIfAbsent(
                jdbcUrl,
                key -> {

                    DataSources.dataSource(jdbcUrl);

                    String persistenceUnitName = key.contains("rococo-auth") ? "rococo-auth" : "rococo-api";

                    Map<String, Object> properties = new HashMap<>();
                    properties.put("jakarta.persistence.jdbc.url", key);

                    properties.put("jakarta.persistence.jdbc.user", CFG.dbUsername());
                    properties.put("jakarta.persistence.jdbc.password", CFG.dbPassword());

                    properties.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");

                    properties.put("hibernate.connection.username", CFG.dbUsername());
                    properties.put("hibernate.connection.password", CFG.dbPassword());

                    properties.put("hibernate.session_factory.interceptor", new AssignedIdInterceptor());

                    return Persistence.createEntityManagerFactory(persistenceUnitName, properties);
                }
        );


        return new ThreadSafeEntityManager(emf);
    }
}
