package ru.otus.web.server;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.security.ConstraintMapping;
import org.eclipse.jetty.ee10.servlet.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.Constraint;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.web.dao.UserDao;
import ru.otus.web.services.TemplateProcessor;
import ru.otus.web.services.UserAuthService;

public class UsersWebServerWithBasicSecurity extends UsersWebServerSimple {
    private static final String ROLE_NAME_USER = "user";
    private static final String ROLE_NAME_ADMIN = "admin";

    private final LoginService loginService;

    public UsersWebServerWithBasicSecurity(
            int port,
            LoginService loginService,
            UserDao userDao,
            DBServiceClient dbServiceClient,
            Gson gson,
            TemplateProcessor templateProcessor,
            UserAuthService userAuthService) {
        super(
                port,
                userDao,
                dbServiceClient,
                gson,
                templateProcessor,
                userAuthService);
        this.loginService = loginService;
    }

    @Override
    protected Handler applySecurity(ServletContextHandler servletContextHandler, String... paths) {
        Constraint constraint = Constraint.from(ROLE_NAME_USER, ROLE_NAME_ADMIN);

        List<ConstraintMapping> constraintMappings = new ArrayList<>();
        Arrays.stream(paths).forEachOrdered(path -> {
            ConstraintMapping mapping = new ConstraintMapping();
            mapping.setPathSpec(path);
            mapping.setConstraint(constraint);
            constraintMappings.add(mapping);
        });

        ConstraintSecurityHandler security = new ConstraintSecurityHandler();
        security.setAuthenticator(new BasicAuthenticator());

        security.setLoginService(loginService);
        security.setConstraintMappings(constraintMappings);
        security.setHandler(new Handler.Wrapper(servletContextHandler));

        return security;
    }
}
