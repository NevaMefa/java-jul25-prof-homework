package ru.otus.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static ru.otus.utils.WebServerHelper.buildUrl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.User;
import ru.otus.web.dao.UserDao;
import ru.otus.web.server.UsersWebServer;
import ru.otus.web.server.UsersWebServerSimple;
import ru.otus.web.services.TemplateProcessor;
import ru.otus.web.services.UserAuthService;
import ru.otus.web.services.UserAuthServiceImpl;

@DisplayName("Тест сервера должен ")
class UsersWebServerImplTest {

    private static final int WEB_SERVER_PORT = 8989;
    private static final String WEB_SERVER_URL = "http://localhost:" + WEB_SERVER_PORT + "/";
    private static final String API_USER_URL = "api/user";

    private static final long DEFAULT_USER_ID = 1L;

    private static final User DEFAULT_USER = new User(DEFAULT_USER_ID, "Vasya", "user1", "11111", false);

    private static Gson gson;
    private static UsersWebServer webServer;
    private static HttpClient http;

    @BeforeAll
    static void setUp() throws Exception {
        http = HttpClient.newHttpClient();

        TemplateProcessor templateProcessor = mock(TemplateProcessor.class);
        UserDao userDao = mock(UserDao.class);

        // Мокаем метод findById для UserDao
        given(userDao.findById(DEFAULT_USER_ID)).willReturn(Optional.of(DEFAULT_USER));

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

        gson = new GsonBuilder().serializeNulls().create();

        // Передаем все параметры в конструктор
        webServer = new UsersWebServerSimple(
                WEB_SERVER_PORT, userDao, dbServiceClient, gson, templateProcessor, authService);
        webServer.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        webServer.stop();
    }

    @DisplayName("возвращать корректные данные при запросе пользователя по id если вход выполнен")
    @Test
    void shouldReturnCorrectUserWhenAuthorized() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(buildUrl(WEB_SERVER_URL, API_USER_URL, String.valueOf(DEFAULT_USER_ID))))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(response.body()).isEqualTo(gson.toJson(DEFAULT_USER));
    }
}
