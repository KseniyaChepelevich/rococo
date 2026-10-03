package io.student.rcc.data.mapper.tpl;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import io.student.rcc.config.Config;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DataSources {
    private DataSources() {
    }

    private static final Config CFG = Config.getInstance();
    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

    public static DataSource dataSource(@Nonnull String jdbcUrl) {
        return dataSources.computeIfAbsent(
                jdbcUrl,
                key -> {
                    AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
                    String dbNameWithParams = StringUtils.substringAfter(jdbcUrl, "3306/");

                    final String uniqId = dbNameWithParams.contains("?")
                            ? StringUtils.substringBefore(dbNameWithParams, "?")
                            : dbNameWithParams;

                    if (StringUtils.isEmpty(uniqId)) {
                        throw new IllegalArgumentException("Failed to extract unique database name from JDBC URL: " + jdbcUrl);
                    }

                    dsBean.setUniqueResourceName(uniqId);

                    dsBean.setXaDataSourceClassName("ocom.mysql.cj.jdbc.MysqlXADataSource");
                    Properties props = new Properties();
                    props.put("URL", jdbcUrl);
                    props.put("user", CFG.dbUsername());
                    props.put("password", CFG.dbPassword());
                    dsBean.setXaProperties(props);
                    dsBean.setPoolSize(3);
                    dsBean.setMaxPoolSize(10);
                    try {
                        InitialContext context = new InitialContext();
                        context.rebind("java:comp/env/jdbc/" + uniqId, dsBean);
                    } catch (NamingException e) {
                        throw new RuntimeException(e);
                    }
                    return dsBean;
                }
        );
    }
}
