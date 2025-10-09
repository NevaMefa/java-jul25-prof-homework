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
import ru.otus.web.server.UsersWebServerWithFilterBasedSecurity;
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

public class WebServerWithFilterBasedSecurityDemo {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";

    public static void main(String[] args) throws Exception {
        // Создаем UserDao
        UserDao userDao = new InMemoryUserDao();

        // Создаем Gson
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

        // Создаем TemplateProcessor
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

        // Создаем DBServiceClient
        DBServiceClient dbServiceClient = new DbServiceClientImpl(transactionManager, clientDataTemplate);

        // Создаем UserAuthService
        UserAuthService authService = new UserAuthServiceImpl(userDao);

        // Передаем все зависимости в конструктор UsersWebServerWithFilterBasedSecurity
        UsersWebServer usersWebServer = new UsersWebServerWithFilterBasedSecurity(
                WEB_SERVER_PORT, // port
                authService, // UserAuthService
                dbServiceClient, // DBServiceClient
                templateProcessor, // TemplateProcessor
                userDao, // UserDao
                gson // Gson
                );

        // Запускаем веб-сервер
        usersWebServer.start();
        usersWebServer.join();
    }
}
