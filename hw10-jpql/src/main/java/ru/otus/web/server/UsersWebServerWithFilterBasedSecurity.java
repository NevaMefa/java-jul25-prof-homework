package ru.otus.web.server;

import com.google.gson.Gson;
import java.util.Arrays;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.web.dao.UserDao;
import ru.otus.web.services.TemplateProcessor;
import ru.otus.web.services.UserAuthService;
import ru.otus.web.servlet.AuthorizationFilter;
import ru.otus.web.servlet.LoginServlet;
import ru.otus.web.servlet.UsersServlet;

public class UsersWebServerWithFilterBasedSecurity extends UsersWebServerSimple {

    private final UserAuthService authService;
    private final DBServiceClient dbServiceClient;

    public UsersWebServerWithFilterBasedSecurity(
            int port,
            UserAuthService authService,
            DBServiceClient dbServiceClient,
            TemplateProcessor templateProcessor,
            UserDao userDao, // Добавляем userDao
            Gson gson) { // Добавляем gson
        // Вызываем конструктор родителя с полным набором параметров
        super(port, userDao, dbServiceClient, gson, templateProcessor, authService);
        this.authService = authService;
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        // добавляем LoginServlet для авторизации
        servletContextHandler.addServlet(new ServletHolder(new LoginServlet(templateProcessor, authService)), "/login");

        // добавляем ClientServlet — админка клиентов
        servletContextHandler.addServlet(
                new ServletHolder(new UsersServlet(templateProcessor, dbServiceClient)), "/admin");

        // подключаем фильтр авторизации
        AuthorizationFilter authorizationFilter = new AuthorizationFilter();
        Arrays.stream(paths)
                .forEachOrdered(
                        path -> servletContextHandler.addFilter(new FilterHolder(authorizationFilter), path, null));

        return servletContextHandler;
    }
}
