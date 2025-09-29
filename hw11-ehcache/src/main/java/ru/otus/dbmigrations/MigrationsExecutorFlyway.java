package ru.otus.dbmigrations;

import org.flywaydb.core.Flyway;
import org.hibernate.cfg.Configuration; // ВАЖНО: этот импорт!

public class MigrationsExecutorFlyway {

    private final String migrationsLocation;

    public MigrationsExecutorFlyway(String migrationsLocation) {
        this.migrationsLocation = migrationsLocation; // пример: "db/migration"
    }

    public void executeMigrations() {
        Configuration configuration = new Configuration().configure("hibernate.cfg.xml");

        String dbUrl = configuration.getProperty("hibernate.connection.url");
        String dbUser = configuration.getProperty("hibernate.connection.username");
        String dbPass = configuration.getProperty("hibernate.connection.password");

        Flyway flyway = Flyway.configure()
                .locations(migrationsLocation)
                .dataSource(dbUrl, dbUser, dbPass)
                .load();

        flyway.migrate();
    }
}
