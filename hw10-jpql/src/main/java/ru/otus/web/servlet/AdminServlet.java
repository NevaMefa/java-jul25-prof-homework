package ru.otus.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.web.services.TemplateProcessor;
import ru.otus.web.services.UserAuthService;

public class AdminServlet extends HttpServlet {

    private final TemplateProcessor templateProcessor;
    private final DBServiceClient dbServiceClient;
    private final UserAuthService userAuthService;

    public AdminServlet(
            TemplateProcessor templateProcessor, DBServiceClient dbServiceClient, UserAuthService userAuthService) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
        this.userAuthService = userAuthService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null
                || session.getAttribute("user") == null
                || !userAuthService.isAdmin((String) session.getAttribute("user"))) {
            response.sendRedirect("/login");
            return;
        }

        var clients = dbServiceClient.findAll();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("clients", clients);
        response.setContentType("text/html");
        response.getWriter().println(templateProcessor.getPage("admin_page.html", paramsMap));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException {
        String clientName = req.getParameter("clientName");
        String clientAddress = req.getParameter("clientAddress");
        String clientPhone = req.getParameter("clientPhone");

        if (clientName != null && !clientName.isEmpty()) {
            Address address = (clientAddress != null && !clientAddress.isEmpty())
                    ? new Address(null, clientAddress)
                    : null;

            Phone phone = (clientPhone != null && !clientPhone.isEmpty()) ? new Phone(null, clientPhone) : null;

            Client newClient = new Client(clientName);

            if (address != null) {
                newClient.setAddress(address);
            }

            if (phone != null) {
                newClient.addPhone(phone);
            }

            dbServiceClient.saveClient(newClient);
        }

        response.sendRedirect("/admin");
    }
}
