package ru.otus.service;

import java.util.List;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.repository.DataTemplateHibernate;
import ru.otus.repository.HibernateUtils;
import ru.otus.sessionmanager.TransactionManagerHibernate;

public class DbServiceDemo {
    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static void main(String[] args) {
        new MigrationsExecutorFlyway("db/migration").executeMigrations();

        SessionFactory sf =
                HibernateUtils.buildSessionFactory("hibernate.cfg.xml", Client.class, Address.class, Phone.class);

        var tx = new TransactionManagerHibernate(sf);
        var template = new DataTemplateHibernate<>(Client.class);

        HwCache<String, Client> cache = new MyCache<>();

        DBServiceClient service = new DbServiceClientImpl(tx, template, cache);

        var saved = service.saveClient(
                new Client(null, "Vasya", new Address(null, "Lenina"), List.of(new Phone(null, "12345"))));
        long id = saved.getId();

        long t1 = System.nanoTime();
        service.getClient(id);
        long t2 = System.nanoTime();
        service.getClient(id);
        long t3 = System.nanoTime();

        log.info("first read (DB): {} ms", (t2 - t1) / 1_000_000);
        log.info("second read (cache): {} ms", (t3 - t2) / 1_000_000);
    }
}
