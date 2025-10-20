package ru.otus.web.servlet;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import ru.otus.web.services.TemplateProcessor;
import ru.otus.web.services.UserAuthService;

@SuppressWarnings({"java:S1989"})
public class LoginServlet extends HttpServlet {

    private static final String PARAM_LOGIN = "login";
    private static final String PARAM_PASSWORD = "password";
    private static final int MAX_INACTIVE_INTERVAL = 30;
    private static final String LOGIN_PAGE_TEMPLATE = "login.html";

    private final transient TemplateProcessor templateProcessor;
    private final transient UserAuthService userAuthService;

    public LoginServlet(TemplateProcessor templateProcessor, UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
        this.templateProcessor = templateProcessor;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {

            String redirectPage = (String) session.getAttribute("redirectPage");
            if (redirectPage == null) {
                redirectPage = "/users";
            }

            response.sendRedirect(redirectPage);
            return;
        }

        response.setContentType("text/html");
        response.getWriter().println(templateProcessor.getPage(LOGIN_PAGE_TEMPLATE, Collections.emptyMap()));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter(PARAM_LOGIN);
        String password = request.getParameter(PARAM_PASSWORD);

        if (userAuthService.authenticate(name, password)) {
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
            session.setAttribute("user", name);

            String redirectPage = (String) session.getAttribute("redirectPage");
            if (redirectPage == null) {
                redirectPage = "/users";
            }

            if (userAuthService.isAdmin(name)) {
                response.sendRedirect("/admin");
            } else {
                response.sendRedirect(redirectPage);
            }

        } else {
            response.setStatus(SC_UNAUTHORIZED);
            Map<String, Object> paramsMap = Collections.singletonMap("error", "Invalid login or password");
            response.setContentType("text/html");
            response.getWriter().println(templateProcessor.getPage(LOGIN_PAGE_TEMPLATE, paramsMap));
        }
    }
}
