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
import ru.otus.web.servlet.AdminServlet;
import ru.otus.web.servlet.AuthorizationFilter;
import ru.otus.web.servlet.LoginServlet;
import ru.otus.web.servlet.UsersServlet;

public class UsersWebServerWithFilterBasedSecurity extends UsersWebServerSimple {

    private final UserAuthService authService;
    private final DBServiceClient dbServiceClient;
    private final UserDao userDao;

    public UsersWebServerWithFilterBasedSecurity(
            int port,
            UserAuthService authService,
            DBServiceClient dbServiceClient,
            TemplateProcessor templateProcessor,
            UserDao userDao,
            Gson gson) {
        super(port, userDao, dbServiceClient, gson, templateProcessor, authService);
        this.authService = authService;
        this.dbServiceClient = dbServiceClient;
        this.userDao = userDao;
    }

    @Override
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        servletContextHandler.addServlet(new ServletHolder(new LoginServlet(templateProcessor, authService)), "/login");

        servletContextHandler.addServlet(
                new ServletHolder(new UsersServlet(templateProcessor, dbServiceClient, userDao, authService)),
                "/users");

        servletContextHandler.addServlet(
                new ServletHolder(new AdminServlet(templateProcessor, dbServiceClient, authService)), "/admin");

        AuthorizationFilter authorizationFilter = new AuthorizationFilter();
        Arrays.stream(paths)
                .forEachOrdered(
                        path -> servletContextHandler.addFilter(new FilterHolder(authorizationFilter), path, null));

        return servletContextHandler;
    }
}
