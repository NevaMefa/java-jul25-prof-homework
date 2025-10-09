package ru.otus.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.otus.crm.model.User;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.web.dao.UserDao;
import ru.otus.web.services.TemplateProcessor;
import ru.otus.web.services.UserAuthService;

@SuppressWarnings({"java:S1989"})
public class UsersServlet extends HttpServlet {

    private static final String USERS_PAGE_TEMPLATE = "users.html";
    private static final String ADMIN_PAGE_TEMPLATE = "admin_page.html";

    private final transient DBServiceClient dbServiceClient;
    private final transient TemplateProcessor templateProcessor;
    private final transient UserDao userDao;
    private final transient UserAuthService userAuthService;

    public UsersServlet(
            TemplateProcessor templateProcessor,
            DBServiceClient dbServiceClient,
            UserDao userDao,
            UserAuthService userAuthService) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
        this.userDao = userDao;
        this.userAuthService = userAuthService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/login");
            return;
        }

        var clients = dbServiceClient.findAll();
        User randomUser = userDao.findRandomUser().orElse(null);

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("clients", clients);
        paramsMap.put("randomUser", randomUser);

        response.setContentType("text/html");
        response.getWriter().println(templateProcessor.getPage(USERS_PAGE_TEMPLATE, paramsMap));
    }
}
