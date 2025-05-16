package org.example.demo;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.sqlite.SQLiteDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Singleton
@Startup
public class SqliteInitializer {

    private static final String DB_FILE_PATH = System.getProperty("user.home") + "/order-service-db.sqlite";
    private static final String JNDI_NAME = "java:jboss/datasources/SqliteDS";

    @PostConstruct
    public void initializeDatabase() {
        try {
            // Ensure database file exists
            File dbFile = new File(DB_FILE_PATH);
            if (!dbFile.exists()) {
                dbFile.getParentFile().mkdirs();
                System.out.println("SQLite database file created at: " + DB_FILE_PATH);
            }

            // Create SQLite DataSource
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl("jdbc:sqlite:" + DB_FILE_PATH);

            // Test connection
            try (Connection conn = dataSource.getConnection()) {
                System.out.println("SQLite connection test successful");
            }

            // Bind to JNDI
            bindToJndi(dataSource);

            System.out.println("SQLite DataSource initialized and bound to JNDI: " + JNDI_NAME);
        } catch (Exception e) {
            System.err.println("Failed to initialize SQLite database:");
            e.printStackTrace();
        }
    }

    private void bindToJndi(DataSource dataSource) throws NamingException {
        Properties jndiProps = new Properties();
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");

        Context context = new InitialContext(jndiProps);
        context.rebind(JNDI_NAME, dataSource);
    }
}