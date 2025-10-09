package ru.otus.web.server;

import com.google.gson.Gson;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.web.dao.UserDao;
import ru.otus.web.helpers.FileSystemHelper;
import ru.otus.web.services.TemplateProcessor;
import ru.otus.web.services.UserAuthService;
import ru.otus.web.servlet.LoginServlet;
import ru.otus.web.servlet.UsersApiServlet;
import ru.otus.web.servlet.UsersServlet;

public class UsersWebServerSimple implements UsersWebServer {
    private static final String START_PAGE_NAME = "index.html";
    private static final String COMMON_RESOURCES_DIR = "static";

    private final UserDao userDao;
    private final DBServiceClient dbServiceClient; // Добавлено
    private final Gson gson;
    protected final TemplateProcessor templateProcessor;
    private final UserAuthService userAuthService; // Добавлено
    private final Server server;

    // Обновленный конструктор с добавленными зависимостями
    public UsersWebServerSimple(
            int port,
            UserDao userDao,
            DBServiceClient dbServiceClient,
            Gson gson,
            TemplateProcessor templateProcessor,
            UserAuthService userAuthService) {
        this.userDao = userDao;
        this.dbServiceClient = dbServiceClient;
        this.gson = gson;
        this.templateProcessor = templateProcessor;
        this.userAuthService = userAuthService;
        this.server = new Server(port);
    }

    @Override
    public void start() throws Exception {
        if (server.getHandlers().isEmpty()) {
            initContext();
        }
        server.start();
    }

    @Override
    public void join() throws Exception {
        server.join();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

    private void initContext() {
        ResourceHandler resourceHandler = createResourceHandler();
        ServletContextHandler servletContextHandler = createServletContextHandler();

        Handler.Sequence sequence = new Handler.Sequence();
        sequence.addHandler(resourceHandler);
        sequence.addHandler(applySecurity(servletContextHandler, "/users", "/api/user/*"));

        server.setHandler(sequence);
    }

    @SuppressWarnings({"squid:S1172"})
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        return servletContextHandler;
    }

    private ResourceHandler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirAllowed(false);
        resourceHandler.setWelcomeFiles(START_PAGE_NAME);
        resourceHandler.setBaseResourceAsString(
                FileSystemHelper.localFileNameOrResourceNameToFullPath(COMMON_RESOURCES_DIR));
        return resourceHandler;
    }

    private ServletContextHandler createServletContextHandler() {
        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        // Передаем dbServiceClient в UsersServlet
        servletContextHandler.addServlet(
                new ServletHolder(new UsersServlet(templateProcessor, dbServiceClient)), "/users");

        // Передаем dbServiceClient и gson в UsersApiServlet
        servletContextHandler.addServlet(new ServletHolder(new UsersApiServlet(userDao, gson)), "/api/user/*");

        // Передаем userAuthService и templateProcessor в LoginServlet
        servletContextHandler.addServlet(
                new ServletHolder(new LoginServlet(templateProcessor, userAuthService)), "/login");

        // Еще один сервлет для админов (передаем dbServiceClient)
        servletContextHandler.addServlet(
                new ServletHolder(new UsersServlet(templateProcessor, dbServiceClient)), "/admin");

        return servletContextHandler;
    }
}
