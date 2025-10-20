package ru.otus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.web.dao.InMemoryUserDao;
import ru.otus.web.dao.UserDao;
import ru.otus.web.server.UsersWebServer;
import ru.otus.web.server.UsersWebServerSimple;
import ru.otus.web.services.TemplateProcessor;
import ru.otus.web.services.TemplateProcessorImpl;
import ru.otus.web.services.UserAuthService;
import ru.otus.web.services.UserAuthServiceImpl;

/*
    Полезные для демо ссылки

    // Стартовая страница
    http://localhost:8080

    // Страница пользователей
    http://localhost:8080/users

    // REST сервис
    http://localhost:8080/api/user/3
*/
public class WebServerSimpleDemo {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";

    public static void main(String[] args) throws Exception {
        UserDao userDao = new InMemoryUserDao();
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        TransactionManager transactionManager = new TransactionManagerHibernate(sessionFactory);
        DataTemplate<Client> clientDataTemplate = new DataTemplateHibernate<>(Client.class);

        DBServiceClient dbServiceClient = new DbServiceClientImpl(transactionManager, clientDataTemplate);
        UserAuthService userAuthService = new UserAuthServiceImpl(userDao);

        UsersWebServer usersWebServer = new UsersWebServerSimple(
                WEB_SERVER_PORT,
                userDao,
                dbServiceClient,
                gson,
                templateProcessor,
                userAuthService
        );

        usersWebServer.start();
        usersWebServer.join();
    }
}
