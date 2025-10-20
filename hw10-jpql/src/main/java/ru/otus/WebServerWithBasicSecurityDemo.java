package ru.otus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.URI;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.util.resource.PathResourceFactory;
import org.eclipse.jetty.util.resource.Resource;
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
import ru.otus.web.helpers.FileSystemHelper;
import ru.otus.web.server.UsersWebServer;
import ru.otus.web.server.UsersWebServerWithBasicSecurity;
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

public class WebServerWithBasicSecurityDemo {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";
    private static final String HASH_LOGIN_SERVICE_CONFIG_NAME = "realm.properties";
    private static final String REALM_NAME = "AnyRealm";

    public static void main(String[] args) throws Exception {
        UserDao userDao = new InMemoryUserDao();
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);

        // Создаем SessionFactory для работы с Hibernate
        SessionFactory sessionFactory = new Configuration()
                .configure() // Загружаем конфигурацию из hibernate.cfg.xml
                .addAnnotatedClass(Client.class) // Добавляем класс Client для маппинга
                .buildSessionFactory(); // Строим SessionFactory

        // Создаем TransactionManager с использованием SessionFactory
        TransactionManager transactionManager = new TransactionManagerHibernate(sessionFactory);

        // Создаем DataTemplate для работы с клиентами
        DataTemplate<Client> clientDataTemplate = new DataTemplateHibernate<>(Client.class);

        // Создаем DBServiceClient и UserAuthService
        DBServiceClient dbServiceClient = new DbServiceClientImpl(transactionManager, clientDataTemplate);
        UserAuthService userAuthService = new UserAuthServiceImpl(userDao);

        String hashLoginServiceConfigPath =
                FileSystemHelper.localFileNameOrResourceNameToFullPath(HASH_LOGIN_SERVICE_CONFIG_NAME);
        PathResourceFactory pathResourceFactory = new PathResourceFactory();
        Resource configResource = pathResourceFactory.newResource(URI.create(hashLoginServiceConfigPath));

        LoginService loginService = new HashLoginService(REALM_NAME, configResource);
        // LoginService loginService = new InMemoryLoginServiceImpl(userDao); // NOSONAR

        // Создаем веб-сервер с базовой аутентификацией
        UsersWebServer usersWebServer = new UsersWebServerWithBasicSecurity(
                WEB_SERVER_PORT,
                loginService,
                userDao,
                dbServiceClient, // Передаем dbServiceClient
                gson,
                templateProcessor,
                userAuthService // Передаем userAuthService
                );

        usersWebServer.start();
        usersWebServer.join();
    }
}
